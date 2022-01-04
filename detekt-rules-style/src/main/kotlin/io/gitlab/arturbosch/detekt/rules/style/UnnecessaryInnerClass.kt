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
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.classId

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

    private val candidateClasses = mutableMapOf<KtClass, Set<ClassId>>()
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
            candidateClasses[klass] = buildParentClassChain(klass)
        }

        // Visit the class to determine whether it contains any references
        // to outer class members.
        super.visitClass(klass)

        if (candidateClasses.contains(klass)) {
            report(
                CodeSmell(
                    issue,
                    Entity.Companion.from(klass),
                    "Class '${klass.name}' does not require `inner` keyword."
                )
            )
            candidateClasses.remove(klass)
        }
        classChain.pop()
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        checkForOuterUsage { parentClasses ->
            property.initializer.belongsToParentClass(parentClasses)
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        checkForOuterUsage { parentClasses ->
            function.initializer.belongsToParentClass(parentClasses)
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        checkForOuterUsage { parentClasses ->
            expression.belongsToParentClass(parentClasses) ||
                expression.collectDescendantsOfType<KtReferenceExpression> {
                    it.belongsToParentClass(parentClasses)
                }.isNotEmpty()
        }
    }

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        checkForOuterUsage { parentClasses ->
            parameter.defaultValue.belongsToParentClass(parentClasses)
        }
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        checkForOuterUsage { parentClasses ->
            expression.left.belongsToParentClass(parentClasses) ||
                expression.right.belongsToParentClass(parentClasses)
        }
    }

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        checkForOuterUsage { parentClasses ->
            val condition = expression.condition
            condition is KtReferenceExpression && condition.belongsToParentClass(parentClasses)
        }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        checkForOuterUsage { parentClasses ->
            expression.receiverExpression.belongsToParentClass(parentClasses)
        }
    }

    // Replace this "constructor().apply{}" pattern with buildSet() when the Kotlin
    // API version is upgraded to 1.6
    private fun buildParentClassChain(klass: KtClass) = HashSet<ClassId>().apply {
        var containingClass = klass.containingClass()
        while (containingClass != null) {
            containingClass.getClassId()?.let { add(it) }
            containingClass = containingClass.containingClass()
        }
    }

    private fun checkForOuterUsage(checkBlock: (Set<ClassId>) -> Boolean) {
        val containingClass = classChain.peek() ?: return
        val parentClasses = candidateClasses[containingClass] ?: return
        if (checkBlock.invoke(parentClasses)) {
            candidateClasses.remove(containingClass)
        }
    }

    private fun KtElement?.belongsToParentClass(parentClasses: Set<ClassId>): Boolean {
        return this?.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.containingDeclaration
            ?.let { (it as? ClassifierDescriptor)?.classId }
            ?.let(parentClasses::contains) == true
    }
}
