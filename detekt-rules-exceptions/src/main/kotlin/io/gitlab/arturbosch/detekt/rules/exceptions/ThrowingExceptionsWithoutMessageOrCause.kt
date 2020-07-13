package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * This rule reports all exceptions which are thrown without arguments or further description.
 * Exceptions should always call one of the constructor overloads to provide a message or a cause.
 * Exceptions should be meaningful and contain as much detail about the error case as possible. This will help to track
 * down an underlying issue in a better way.
 *
 * <noncompliant>
 * fun foo(bar: Int) {
 *     if (bar < 1) {
 *         throw IllegalArgumentException()
 *     }
 *     // ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(bar: Int) {
 *     if (bar < 1) {
 *         throw IllegalArgumentException("bar must be greater than zero")
 *     }
 *     // ...
 * }
 * </compliant>
 *
 * @configuration exceptions - exceptions which should not be thrown without message or cause
 * (default: `- IllegalArgumentException
 *            - IllegalStateException
 *            - IOException`)
 */
class ThrowingExceptionsWithoutMessageOrCause(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ThrowingExceptionsWithoutMessageOrCause", Severity.Warning,
            "A call to the default constructor of an exception was detected. " +
                    "Instead one of the constructor overloads should be called. " +
                    "This allows to provide more meaningful exceptions.",
            Debt.FIVE_MINS)

    private val exceptions = valueOrDefaultCommaSeparated(EXCEPTIONS, emptyList())

    override fun visitCallExpression(expression: KtCallExpression) {
        val calleeExpressionText = expression.calleeExpression?.text
        if (exceptions.any { calleeExpressionText?.equals(it, ignoreCase = true) == true } &&
            expression.valueArguments.isEmpty()) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
        super.visitCallExpression(expression)
    }

    companion object {
        const val EXCEPTIONS = "exceptions"
    }
}
