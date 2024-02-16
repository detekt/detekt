package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
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
            report(CodeSmell(Entity.from(expression), description))
        }
    }

    override fun visitCatchSection(catchClause: KtCatchClause) {
        catchClause.catchBody?.forEachDescendantOfType<KtNameReferenceExpression> {
            if (it.text == catchClause.catchParameter?.name && hasPrintStacktraceCallExpression(it)) {
                report(CodeSmell(Entity.from(it), description))
            }
        }
    }

    private fun hasPrintStacktraceCallExpression(expression: KtNameReferenceExpression): Boolean {
        val methodCall = expression.nextSibling?.nextSibling
        return methodCall is KtCallExpression && methodCall.text.startsWith("printStackTrace(")
    }
}
