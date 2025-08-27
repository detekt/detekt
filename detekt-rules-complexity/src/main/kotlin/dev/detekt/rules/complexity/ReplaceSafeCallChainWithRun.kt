package dev.detekt.rules.complexity

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

/**
 * Chains of safe calls on non-nullable types are redundant and can be removed by enclosing the redundant safe calls in
 * a `run {}` block. This improves code coverage and reduces cyclomatic complexity as redundant null checks are removed.
 *
 * This rule only checks from the end of a chain and works backwards, so it won't recommend inserting run blocks in the
 * middle of a safe call chain as that is likely to make the code more difficult to understand.
 *
 * The rule will check for every opportunity to replace a safe call when it sits at the end of a chain, even if there's
 * only one, as that will still improve code coverage and reduce cyclomatic complexity.
 *
 * <noncompliant>
 * val x = System.getenv()
 *             ?.getValue("HOME")
 *             ?.toLowerCase()
 *             ?.split("/") ?: emptyList()
 * </noncompliant>
 *
 * <compliant>
 * val x = getenv()?.run {
 *     getValue("HOME")
 *         .toLowerCase()
 *         .split("/")
 * } ?: emptyList()
 * </compliant>
 *
 */
class ReplaceSafeCallChainWithRun(config: Config) :
    Rule(
        config,
        "Chains of safe calls on non-nullable types can be surrounded with `run {}`."
    ),
    RequiresAnalysisApi {

    override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
        super.visitSafeQualifiedExpression(expression)

        /* We want the last safe qualified expression in the chain, so if there are more in this chain then there's no
        need to run checks on this one */
        if (expression.parent is KtSafeQualifiedExpression) return

        var counter = 0

        var receiver = expression.receiverExpression
        while (receiver is KtSafeQualifiedExpression) {
            val canBeNull = analyze(receiver) {
                val call = receiver.resolveToCall()?.successfulCallOrNull<KaCallableMemberCall<*, *>>()
                call != null && call.partiallyAppliedSymbol.signature.returnType.canBeNull
            }
            if (canBeNull) break
            counter++
            receiver = receiver.receiverExpression
        }

        if (counter >= 1) report(Finding(Entity.from(expression), description))
    }
}
