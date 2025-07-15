package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * Reports all calls to explicitly trigger the Garbage Collector.
 * Code should work independently of the garbage collector and should not require the GC to be triggered in certain
 * points in time.
 *
 * <noncompliant>
 * System.gc()
 * Runtime.getRuntime().gc()
 * System.runFinalization()
 * </noncompliant>
 */
@ActiveByDefault(since = "1.0.0")
class ExplicitGarbageCollectionCall(config: Config) : Rule(
    config,
    "Don't try to be smarter than the JVM. Your code should work independently whether the garbage " +
        "collector is disabled or not. If you face memory issues, " +
        "try tuning the JVM options instead of relying on code itself."
) {

    override fun visitCallExpression(expression: KtCallExpression) {
        expression.getCallNameExpression()?.let {
            matchesGCCall(expression, it)
        }
    }

    private fun matchesGCCall(expression: KtCallExpression, it: KtSimpleNameExpression) {
        if (it.textMatches("gc") || it.textMatches("runFinalization")) {
            it.getReceiverExpression()?.let {
                when (it.text) {
                    "System", "Runtime.getRuntime()" -> report(
                        Finding(
                            Entity.from(expression),
                            "An explicit call to the Garbage Collector as in ${it.text} should not be made."
                        )
                    )
                }
            }
        }
    }
}
