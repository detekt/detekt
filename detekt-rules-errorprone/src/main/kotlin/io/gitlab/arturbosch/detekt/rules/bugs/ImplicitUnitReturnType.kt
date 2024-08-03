package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.hasImplicitUnitReturnType
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Functions using expression statements have an implicit return type.
 * Changing the type of the expression accidentally, changes the functions return type.
 * This may lead to backward incompatibility.
 * Use a block statement to make clear this function will never return a value.
 *
 * <noncompliant>
 * fun errorProneUnit() = println("Hello Unit")
 * fun errorProneUnitWithParam(param: String) = param.run { println(this) }
 * fun String.errorProneUnitWithReceiver() = run { println(this) }
 * </noncompliant>
 *
 * <compliant>
 * fun blockStatementUnit() {
 *     // code
 * }
 *
 * // explicit Unit is compliant by default; can be configured to enforce block statement
 * fun safeUnitReturn(): Unit = println("Hello Unit")
 * </compliant>
 *
 */
class ImplicitUnitReturnType(config: Config) :
    Rule(
        config,
        "Functions using expression statements have an implicit return type. " +
            "Changing the type of the expression accidentally, changes the function return type. " +
            "This may lead to backward incompatibility. " +
            "Use a block statement to make clear this function will never return a value."
    ),
    RequiresTypeResolution {
    @Configuration("if functions with explicit `Unit` return type should be allowed")
    private val allowExplicitReturnType: Boolean by config(true)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (allowExplicitReturnType && function.hasDeclaredReturnType()) {
            return
        }

        if (function.bodyExpression?.text == "Unit") return

        if (function.hasImplicitUnitReturnType(bindingContext)) {
            val message = buildString {
                append("'${function.name}'  has the implicit return type `Unit`.")
                append(" Prefer using a block statement")
                if (allowExplicitReturnType) {
                    append(" or specify the return type explicitly")
                }
                append('.')
            }
            report(
                CodeSmell(
                    Entity.atName(function),
                    message
                )
            )
        }
    }
}
