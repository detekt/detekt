package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.isNullable

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
    RequiresTypeResolution {
    override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
        super.visitSafeQualifiedExpression(expression)

        /* We want the last safe qualified expression in the chain, so if there are more in this chain then there's no
        need to run checks on this one */
        if (expression.parent is KtSafeQualifiedExpression) return

        var counter = 0

        var receiver = expression.receiverExpression
        while (receiver is KtSafeQualifiedExpression) {
            if (receiver.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType?.isNullable() == true) break
            counter++
            receiver = receiver.receiverExpression
        }

        if (counter >= 1) report(CodeSmell(Entity.from(expression), description))
    }
}
