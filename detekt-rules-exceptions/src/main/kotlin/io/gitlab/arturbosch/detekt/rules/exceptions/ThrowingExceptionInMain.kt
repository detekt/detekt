package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.psi.isMainFunction
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
