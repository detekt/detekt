package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
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
class UnnecessaryInnerClass(config: Config) :
    Rule(
        config,
        "The 'inner' qualifier is unnecessary."
    ),
    RequiresTypeResolution {
    private val candidateClassToParentClasses = mutableMapOf<KtClass, List<KtClass>>()
    private val classChain = ArrayDeque<KtClass>()

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
                    Entity.Companion.from(klass),
                    "Class '${klass.name}' does not require `inner` keyword."
                )
            )
            candidateClassToParentClasses.remove(klass)
        }
        classChain.removeLast()
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        checkForOuterUsage { findResolvedContainingClassId(expression) }
    }

    override fun visitThisExpression(expression: KtThisExpression) {
        checkForOuterUsage { expression.referenceClassId() }
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

    private fun checkForOuterUsage(getTargetClassId: () -> ClassId?) {
        val currentClass = classChain.lastOrNull() ?: return
        val parentClasses = candidateClassToParentClasses[currentClass] ?: return

        val targetClassId = getTargetClassId() ?: return
        /*
         * If class A -> inner class B -> inner class C, and class C has outer usage of A,
         * then both B and C should stay as inner classes.
         */
        val index = parentClasses.indexOfFirst { it.getClassId() == targetClassId }
        if (index >= 0) {
            candidateClassToParentClasses.remove(currentClass)
            parentClasses.subList(0, index).forEach { candidateClassToParentClasses.remove(it) }
        }
    }

    private fun findResolvedContainingClassId(expression: KtReferenceExpression): ClassId? =
        (bindingContext[BindingContext.REFERENCE_TARGET, expression]?.containingDeclaration as? ClassifierDescriptor)
            ?.classId

    private fun KtThisExpression.referenceClassId(): ClassId? =
        getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.returnType
            ?.constructor
            ?.declarationDescriptor
            ?.classId
}
