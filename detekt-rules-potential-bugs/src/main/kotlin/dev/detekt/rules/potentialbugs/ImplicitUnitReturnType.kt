package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.hasImplicitUnitReturnType
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
    RequiresAnalysisApi {

    @Configuration("if functions with explicit `Unit` return type should be allowed")
    private val allowExplicitReturnType: Boolean by config(true)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (allowExplicitReturnType && function.hasDeclaredReturnType()) {
            return
        }

        if (function.bodyExpression?.text == "Unit") return

        if (function.hasImplicitUnitReturnType()) {
            val message = buildString {
                append("'${function.name}'  has the implicit return type `Unit`.")
                append(" Prefer using a block statement")
                if (allowExplicitReturnType) {
                    append(" or specify the return type explicitly")
                }
                append('.')
            }
            report(
                Finding(
                    Entity.atName(function),
                    message
                )
            )
        }
    }
}
