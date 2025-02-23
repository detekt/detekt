package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

/**
 * This rule reports thrown exceptions that have a type that is too generic. It should be preferred to throw specific
 * exceptions to the case that has currently occurred.
 *
 * <noncompliant>
 * fun foo(bar: Int) {
 *     if (bar < 1) {
 *         throw Exception() // too generic exception thrown here
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
@ActiveByDefault(since = "1.0.0")
class TooGenericExceptionThrown(config: Config) : Rule(
    config,
    "The thrown exception is too generic. Prefer throwing project specific exceptions to handle error cases."
) {

    @Configuration("exceptions which are too generic and should not be thrown")
    private val exceptionNames: Set<String> by config(
        listOf(
            "Error",
            "Exception",
            "RuntimeException",
            "Throwable",
        )
    ) { it.toSet() }

    override fun visitThrowExpression(expression: KtThrowExpression) {
        expression.thrownExpression?.referenceExpression()?.text?.let {
            if (it in exceptionNames) {
                report(
                    Finding(
                        Entity.from(expression),
                        "$it is a too generic Exception. " +
                            "Prefer throwing specific exceptions that indicate a specific error case."
                    )
                )
            }
        }
        super.visitThrowExpression(expression)
    }
}
