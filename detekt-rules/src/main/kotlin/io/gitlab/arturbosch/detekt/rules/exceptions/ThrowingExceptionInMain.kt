package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isMainFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

/**
 * This rule reports all exceptions that are thrown in a `main` method.
 * An exception should only be thrown if it can be handled by a "higher" function.
 *
 * <noncompliant>
 * fun main(args: Array<String>) {
 *     // ...
 *     throw IOException() // exception should not be thrown here
 * }
 * </noncompliant>
 */
class ThrowingExceptionInMain(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ThrowingExceptionInMain", Severity.CodeSmell,
            "The main method should not throw an exception.", Debt.TWENTY_MINS)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.isMainFunction() && containsThrowExpression(function)) {
            report(CodeSmell(issue, Entity.atName(function), issue.description))
        }
    }

    private fun containsThrowExpression(function: KtNamedFunction) =
        function.bodyExpression?.anyDescendantOfType<KtThrowExpression>() == true
}
