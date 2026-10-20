package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

/**
 * Reports a null check on the result of one or more safe calls.
 * Move the same null check to the value that can be null. Later calls can then use `.`.
 * If several values can be null, choose the right check and message for each value.
 *
 * <noncompliant>
 * class A(val b: B?)
 * class B(val value: String)
 *
 * fun test(a: A) {
 *     val lateAssertion = a.b?.value!!
 *     val lateRequirementCheck = requireNotNull(a.b?.value)
 *     val lateStateCheck = checkNotNull(a.b?.value)
 * }
 * </noncompliant>
 *
 * <compliant>
 * class A(val b: B?)
 * class B(val value: String)
 *
 * fun test(a: A) {
 *     val earlyAssertion = a.b!!.value
 *     val earlyRequirementCheck = requireNotNull(a.b).value
 *     val earlyStateCheck = checkNotNull(a.b).value
 * }
 * </compliant>
 */
class UnnecessaryChainedSafeCall(config: Config) :
    Rule(
        config,
        "A null check occurs after a safe call chain. Check nullable values before later calls."
    ),
    RequiresAnalysisApi {

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        super.visitPostfixExpression(expression)
        if (expression.operationToken != KtTokens.EXCLEXCL) return
        if (expression.baseExpression !is KtSafeQualifiedExpression) return

        val check = expression.operationReference.text
        report(Finding(Entity.from(expression), findingMessage(check)))
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (!expression.isCalling(kotlinNullCheckFunctions)) return
        if (expression.checkedValue() !is KtSafeQualifiedExpression) return

        val check = expression.calleeExpression?.text ?: return
        report(Finding(Entity.from(expression), findingMessage(check)))
    }

    private fun KtCallExpression.checkedValue(): KtExpression? = valueArguments.firstOrNull()?.getArgumentExpression()

    private fun findingMessage(check: String): String =
        "`$check` evaluates the result of the entire safe call chain. " +
            "Assert non-nullability on the specific nullable values before chaining further calls."

    companion object {
        private val kotlinNullCheckFunctions = listOf(
            CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("requireNotNull")),
            CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("checkNotNull")),
        )
    }
}
