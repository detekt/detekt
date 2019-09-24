package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * This rule allows to set a list of forbidden methods. This can be used to discourage the use of unstable, experimental
 * or deprecated methods, especially for methods imported from external libraries.
 * Detekt will then report all methods invocation that are forbidden.
 *
 * <noncompliant>
 * import java.lang.System
 * fun main() {
 *    System.gc()
 * }
 * </noncompliant>
 *
 * @configuration methods - Fully qualified method signatures which should not be used (default: `''`)
 */
class ForbiddenMethod(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Mark forbidden methods. A forbidden method could be an invocation of an unstable / experimental " +
                "method and hence you might want to mark it as forbidden in order to get warned about the usage.",
        Debt.TEN_MINS
    )

    private val forbiddenMethods = valueOrDefault(METHODS, "").split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.split(".") }
        .map { it.subList(0, it.size - 1).joinToString(".") to it.last() }

    override fun visitCallExpression(expression: KtCallExpression) {
        val callNameExpression = expression.getCallNameExpression()
        val callNameText = callNameExpression?.text
        val receiverText = callNameExpression?.getReceiverExpression()?.text ?: ""
        if (receiverText to callNameText in forbiddenMethods) {
            report(
                CodeSmell(
                    issue, Entity.from(expression), "The method " +
                            "$callNameText.$receiverText has been forbidden in the Detekt config."
                )
            )
        }
    }

    companion object {
        const val METHODS = "methods"
    }
}
