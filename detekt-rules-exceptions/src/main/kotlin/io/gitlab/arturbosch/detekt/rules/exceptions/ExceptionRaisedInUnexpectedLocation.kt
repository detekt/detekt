package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

/**
 * This rule reports functions which should never throw an exception. If a function exists that does throw
 * an exception it will be reported. By default, this rule checks `toString`, `hashCode`, `equals` and
 * `finalize`. This rule is configurable via the `methodNames` configuration to change the list of functions which
 * should not throw any exceptions.
 *
 * <noncompliant>
 * class Foo {
 *
 *     override fun toString(): String {
 *         throw IllegalStateException() // exception should not be thrown here
 *     }
 * }
 * </noncompliant>
 */
@ActiveByDefault(since = "1.16.0")
class ExceptionRaisedInUnexpectedLocation(config: Config) : Rule(config) {

    override val issue = Issue(
        "ExceptionRaisedInUnexpectedLocation",
        "This method is not expected to throw exceptions. This can cause weird behavior.",
    )

    @Configuration("methods which should not throw exceptions")
    private val methodNames: List<String> by config(
        listOf(
            "equals",
            "finalize",
            "hashCode",
            "toString",
        )
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (isPotentialMethod(function) && hasThrowExpression(function.bodyExpression)) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    issue.description
                )
            )
        }
    }

    private fun isPotentialMethod(function: KtNamedFunction) = methodNames.any { function.name == it }

    private fun hasThrowExpression(declaration: KtExpression?) =
        declaration?.anyDescendantOfType<KtThrowExpression>() == true
}
