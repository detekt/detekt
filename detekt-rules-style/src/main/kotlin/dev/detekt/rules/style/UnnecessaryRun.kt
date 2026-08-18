package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * A receiverless `run { ... }` block is used to execute a block and return its result, but if the block only
 * contains a single expression, the `run` call adds unnecessary visual complexity and can be replaced with that
 * expression directly.
 *
 * This rule only reports the non-extension `run` function, i.e. `run { ... }` without a receiver. `T.run { ... }`
 * as well as other scope functions such as `with`, `apply` and `let` are out of scope for this rule.
 *
 * <noncompliant>
 * val value = run { calculateValue() }
 * </noncompliant>
 *
 * <compliant>
 * val value = calculateValue()
 *
 * // `run` is kept as it contains more than one statement
 * val value = run {
 *     first()
 *     second()
 * }
 *
 * // `T.run` is out of scope for this rule
 * val value = receiver.run { calculateValue() }
 * </compliant>
 */
class UnnecessaryRun(config: Config) :
    Rule(
        config,
        "The `run` usage is unnecessary and can be removed."
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != "run") return
        if (!expression.hasSingleExpressionBody()) return

        analyze(expression) {
            val call = expression.resolveToCall()?.singleFunctionCallOrNull() ?: return
            if (call.symbol.callableId != RUN_CALLABLE_ID) return
            if (call.dispatchReceiver != null || call.extensionReceiver != null) return

            report(Finding(Entity.from(expression), "run expression can be omitted"))
        }
    }

    /**
     * Returns whether the lambda body of this call consists of exactly one statement that is an actual
     * expression (as opposed to a local declaration, which would change meaning if hoisted out of the block).
     */
    private fun KtCallExpression.hasSingleExpressionBody(): Boolean {
        val lambda = lambdaArguments.firstOrNull()?.getLambdaExpression() ?: return false
        val statement = lambda.bodyExpression?.statements?.singleOrNull() ?: return false
        return statement !is KtDeclaration
    }

    companion object {
        private val RUN_CALLABLE_ID = CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("run"))
    }
}
