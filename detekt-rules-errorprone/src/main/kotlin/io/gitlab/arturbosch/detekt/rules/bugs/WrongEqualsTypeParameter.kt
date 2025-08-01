package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.psi.hasCorrectEqualsParameter
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Reports equals() methods which take in a wrongly typed parameter.
 * Correct implementations of the equals() method should only take in a parameter of type Any?
 * See: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html
 *
 * <noncompliant>
 * class Foo {
 *
 *     fun equals(other: String): Boolean {
 *         return super.equals(other)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Foo {
 *
 *     fun equals(other: Any?): Boolean {
 *         return super.equals(other)
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class WrongEqualsTypeParameter(config: Config) : Rule(
    config,
    "Wrong parameter type for `equals()` method found. To correctly override the `equals()` method use `Any?`."
) {

    override fun visitClass(klass: KtClass) {
        if (klass.isInterface()) {
            return
        }
        super.visitClass(klass)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.name == "equals" && !function.isTopLevel && function.hasWrongEqualsSignature()) {
            report(
                Finding(
                    Entity.atName(function),
                    "equals() methods should only take one parameter " +
                        "of type Any?."
                )
            )
        }
    }

    private fun KtNamedFunction.hasWrongEqualsSignature() =
        valueParameters.size == 1 && !hasCorrectEqualsParameter()
}
