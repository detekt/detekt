package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

/**
 * This rule allows to define functions which should never throw an exception. If a function exists that does throw
 * an exception it will be reported. By default this rule is checking for `toString`, `hashCode`, `equals` and
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
 *
 * @configuration methodNames - methods which should not throw exceptions
 * (default: `[toString, hashCode, equals, finalize]`)
 */
class ExceptionRaisedInUnexpectedLocation(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ExceptionRaisedInUnexpectedLocation", Severity.CodeSmell,
            "This method is not expected to throw exceptions. This can cause weird behavior.",
            Debt.TWENTY_MINS)

    private val methods = valueOrDefaultCommaSeparated(
        METHOD_NAMES,
        listOf("toString", "hashCode", "equals", "finalize"))

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

    private fun isPotentialMethod(function: KtNamedFunction) = methods.any { function.name == it }

    private fun hasThrowExpression(declaration: KtExpression?) =
            declaration?.anyDescendantOfType<KtThrowExpression>() == true

    companion object {
        const val METHOD_NAMES = "methodNames"
    }
}
