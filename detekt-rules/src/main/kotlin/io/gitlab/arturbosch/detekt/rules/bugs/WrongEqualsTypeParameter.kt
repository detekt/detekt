package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.hasCorrectEqualsParameter
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
 *
 * @active since v1.2.0
 */
class WrongEqualsTypeParameter(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "WrongEqualsTypeParameter",
        Severity.Defect,
        "Wrong parameter type for equals() method found. " +
            "To correctly override the equals() method use Any?",
        Debt.TEN_MINS
    )

    override fun visitClass(klass: KtClass) {
        if (klass.isInterface()) {
            return
        }
        super.visitClass(klass)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.name == "equals" && !function.isTopLevel && function.hasWrongEqualsSignature()) {
            report(
                CodeSmell(
                    issue,
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
