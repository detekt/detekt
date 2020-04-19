package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.valueOrDefaultCommaSeparated
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

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
 * @configuration methods - Comma separated list of fully qualified method signatures which are forbidden (default: `[]`)
 */
class ForbiddenMethodCall(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Mark forbidden methods. A forbidden method could be an invocation of an unstable / experimental " +
                "method and hence you might want to mark it as forbidden in order to get warned about the usage.",
        Debt.TEN_MINS
    )

    private val forbiddenMethods = valueOrDefaultCommaSeparated(METHODS, emptyList())

    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        val fqName = resolvedCall.resultingDescriptor.fqNameOrNull()?.asString()

        if (fqName != null && fqName in forbiddenMethods) {
            report(
                CodeSmell(
                    issue, Entity.from(expression), "The method $fqName has been forbidden in the Detekt config."
                )
            )
        }
    }

    companion object {
        const val METHODS = "methods"
    }
}
