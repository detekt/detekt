package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass

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
    RequiresAnalysisApi {

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
                Finding(
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

    private fun findParentClasses(ktClass: KtClass): List<KtClass> =
        buildList {
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
        analyze(expression) {
            expression.resolveToCall()
                ?.successfulCallOrNull<KaCallableMemberCall<*, *>>()
                ?.partiallyAppliedSymbol
                ?.dispatchReceiver
                ?.type
                ?.symbol
                ?.classId
        }

    private fun KtThisExpression.referenceClassId(): ClassId? =
        analyze(this) {
            expressionType?.symbol?.classId
        }
}
