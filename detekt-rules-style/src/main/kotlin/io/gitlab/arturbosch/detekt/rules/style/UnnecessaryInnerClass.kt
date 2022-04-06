package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.backend.common.peek
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

/**
 * This rule reports unnecessary inner classes. Nested classes that do not access members from the outer class do
 * not require the `inner` qualifier.
 *
 * <noncompliant>
 * class A {
 *     val foo = "BAR"
 *
 *     inner class B {
 *         val fizz = "BUZZ"
 *
 *         fun printFizz() {
 *             println(fizz)
 *         }
 *     }
 * }
 * </noncompliant>
 */
@Suppress("TooManyFunctions")
@RequiresTypeResolution
class UnnecessaryInnerClass(config: Config = Config.empty) : Rule(config) {

    private val candidateClassToParentClasses = mutableMapOf<KtClass, List<KtClass>>()
    private val classChain = ArrayDeque<KtClass>()

    override val issue: Issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "The 'inner' qualifier is unnecessary.",
        Debt.FIVE_MINS
    )

    override fun visit(root: KtFile) {
        if (bindingContext == BindingContext.EMPTY) return
        super.visit(root)
    }

    override fun visitClass(klass: KtClass) {
        classChain.add(klass)
        if (klass.isInner()) {
            candidateClassToParentClasses[klass] = findParentClasses(klass)
        }

        // Visit the class to determine whether it contains any references
        // to outer class members.
        super.visitClass(klass)

        if (klass.isInner() && candidateClassToParentClasses.contains(klass)) {
            report(
                CodeSmell(
                    issue,
                    Entity.Companion.from(klass),
                    "Class '${klass.name}' does not require `inner` keyword."
                )
            )
            candidateClassToParentClasses.remove(klass)
        }
        classChain.pop()
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        checkForOuterUsage(listOfNotNull(property.initializer))
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        checkForOuterUsage(listOfNotNull(function.initializer))
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        checkForOuterUsage(expression.collectDescendantsOfType<KtReferenceExpression>() + expression)
    }

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        checkForOuterUsage(listOfNotNull(parameter.defaultValue))
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        checkForOuterUsage(listOfNotNull(expression.left, expression.right))
    }

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        checkForOuterUsage(listOfNotNull(expression.condition as? KtReferenceExpression))
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        checkForOuterUsage(listOf(expression.receiverExpression))
    }

    // Replace this "constructor().apply{}" pattern with buildList() when the Kotlin
    // API version is upgraded to 1.6
    private fun findParentClasses(ktClass: KtClass): List<KtClass> = ArrayList<KtClass>().apply {
        var containingClass = ktClass.containingClass()
        while (containingClass != null) {
            add(containingClass)
            containingClass = containingClass.containingClass()
        }
    }

    private fun checkForOuterUsage(expressionsToResolve: List<KtElement>) {
        val currentClass = classChain.peek() ?: return
        val parentClasses = candidateClassToParentClasses[currentClass] ?: return

        expressionsToResolve.forEach { ktElement ->
            val resolvedContainingClassId = findResolvedContainingClassId(ktElement)
            /*
             * If class A -> inner class B -> inner class C, and class C has outer usage of A,
             * then both B and C should stay as inner classes.
             */
            val index = parentClasses.indexOfFirst { it.getClassId() == resolvedContainingClassId }
            if (index >= 0) {
                candidateClassToParentClasses.remove(currentClass)
                parentClasses.subList(0, index).forEach { candidateClassToParentClasses.remove(it) }
            }
        }
    }

    private fun findResolvedContainingClassId(ktElement: KtElement): ClassId? {
        return ktElement.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.containingDeclaration
            ?.safeAs<ClassifierDescriptor>()
            ?.classId
    }
}
