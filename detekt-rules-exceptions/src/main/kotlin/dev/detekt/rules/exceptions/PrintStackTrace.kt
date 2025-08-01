package dev.detekt.rules.exceptions

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * This rule reports code that tries to print the stacktrace of an exception. Instead of simply printing a stacktrace
 * a better logging solution should be used.
 *
 * <noncompliant>
 * fun foo() {
 *     Thread.dumpStack()
 * }
 *
 * fun bar() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         e.printStackTrace()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * val LOGGER = Logger.getLogger()
 *
 * fun bar() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         LOGGER.info(e)
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class PrintStackTrace(config: Config) : Rule(
    config,
    "Do not print a stack trace. " +
        "These debug statements should be removed or replaced with a logger."
) {

    override fun visitCallExpression(expression: KtCallExpression) {
        val callNameExpression = expression.getCallNameExpression()
        if (callNameExpression?.text == "dumpStack" &&
            callNameExpression.getReceiverExpression()?.text == "Thread"
        ) {
            report(Finding(Entity.from(expression), description))
        }
    }

    override fun visitCatchSection(catchClause: KtCatchClause) {
        catchClause.catchBody?.forEachDescendantOfType<KtNameReferenceExpression> {
            if (it.text == catchClause.catchParameter?.name && hasPrintStacktraceCallExpression(it)) {
                report(Finding(Entity.from(it), description))
            }
        }
    }

    private fun hasPrintStacktraceCallExpression(expression: KtNameReferenceExpression): Boolean {
        val methodCall = expression.nextSibling?.nextSibling
        return methodCall is KtCallExpression && methodCall.text.startsWith("printStackTrace(")
    }
}
