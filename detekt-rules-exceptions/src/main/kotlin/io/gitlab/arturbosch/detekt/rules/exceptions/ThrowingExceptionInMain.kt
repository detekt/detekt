package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
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
class ThrowingExceptionInMain(config: Config) : Rule(
    config,
    "The main method should not throw an exception."
) {

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.isMainFunction() && containsThrowExpression(function)) {
            report(Finding(Entity.atName(function), description))
        }
    }

    private fun containsThrowExpression(function: KtNamedFunction) =
        function.bodyExpression?.anyDescendantOfType<KtThrowExpression>() == true
}
