package dev.detekt.rules.potentialbugs

import com.intellij.psi.PsiElement
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.psi.isEqualsFunction
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Reports `equals()` methods which will always return true or false.
 *
 * Equals methods should always report if some other object is equal to the current object.
 * See the Kotlin documentation for Any.equals(other: Any?):
 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html
 *
 * <noncompliant>
 * override fun equals(other: Any?): Boolean {
 *     return true
 * }
 * </noncompliant>
 *
 * <compliant>
 * override fun equals(other: Any?): Boolean {
 *     return this === other
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class EqualsAlwaysReturnsTrueOrFalse(config: Config) :
    Rule(
        config,
        "Having an `equals()` method that always returns true or false is not a good idea. " +
            "It does not follow the contract of this method. " +
            "Consider a good default implementation (e.g. `this == other`)."
    ) {

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.isEqualsFunction() && function.returnsBooleanConstant()) {
            report(
                Finding(
                    Entity.atName(function),
                    "This equals function always returns the same " +
                        "result regardless of the input parameters."
                )
            )
        }
    }

    private fun KtNamedFunction.returnsBooleanConstant(): Boolean {
        val bodyExpression = bodyExpression ?: return false
        return if (bodyExpression is KtConstantExpression) {
            bodyExpression.isBooleanConstant()
        } else {
            isSingleReturnWithBooleanConstant(bodyExpression)
        }
    }

    private fun isSingleReturnWithBooleanConstant(bodyExpression: KtExpression): Boolean {
        val returnExpressionsInBlock = bodyExpression.collectDescendantsOfType<KtReturnExpression> {
            it.parent == bodyExpression || it.parent is KtAnnotatedExpression && it.parent.parent == bodyExpression
        }
        val lastValidReturnExpression = returnExpressionsInBlock.firstOrNull()?.returnedExpression
        val allReturnExpressions = bodyExpression.collectDescendantsOfType<KtReturnExpression>()
        val hasNoNestedReturnExpression = allReturnExpressions.size == returnExpressionsInBlock.size
        return lastValidReturnExpression?.isBooleanConstant() == true &&
            (
                hasNoNestedReturnExpression ||
                    allReturnExpressions.all { it.returnedExpression?.text == lastValidReturnExpression.text }
                )
    }

    private fun PsiElement.isBooleanConstant() = node.elementType == KtNodeTypes.BOOLEAN_CONSTANT
}
