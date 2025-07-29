package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
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
 */
@ActiveByDefault(since = "1.16.0")
class ThrowingExceptionsWithoutMessageOrCause(config: Config) : Rule(
    config,
    "A call to the default constructor of an exception was detected. " +
        "Instead one of the constructor overloads should be called. " +
        "This allows to provide more meaningful exceptions."
) {

    @Configuration("exceptions which should not be thrown without message or cause")
    private val exceptions: List<String> by config(
        listOf(
            "ArrayIndexOutOfBoundsException",
            "Exception",
            "IllegalArgumentException",
            "IllegalMonitorStateException",
            "IllegalStateException",
            "IndexOutOfBoundsException",
            "NullPointerException",
            "RuntimeException",
            "Throwable",
        )
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val calleeExpressionText = expression.calleeExpression?.text ?: return

        if (exceptions.any { calleeExpressionText == it } && expression.valueArguments.isEmpty()) {
            report(Finding(Entity.from(expression), description))
        }
    }
}
