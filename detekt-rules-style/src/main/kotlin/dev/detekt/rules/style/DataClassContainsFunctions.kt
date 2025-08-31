package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isOperator
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule reports functions inside data classes which have not been marked as a conversion function.
 *
 * Data classes should mainly be used to store data. This rule assumes that they should not contain any extra functions
 * aside functions that help with converting objects from/to one another.
 * Data classes will automatically have a generated `equals`, `toString` and `hashCode` function by the compiler.
 *
 * <noncompliant>
 * data class DataClassWithFunctions(val i: Int) {
 *     fun foo() { }
 * }
 * </noncompliant>
 */
class DataClassContainsFunctions(config: Config) : Rule(
    config,
    "Data classes should mainly be used to store data and should not have any extra functions " +
        "(Compiler will automatically generate equals, toString and hashCode functions)."
) {

    @Configuration("allowed conversion function names")
    private val conversionFunctionPrefix: List<String> by config(listOf("to"))

    @Configuration("allows overloading an operator")
    private val allowOperators by config(false)

    override fun visitClass(klass: KtClass) {
        if (klass.isData()) {
            klass.body?.declarations
                ?.filterIsInstance<KtNamedFunction>()
                ?.forEach { checkFunction(klass, it) }
        }
        super.visitClass(klass)
    }

    private fun checkFunction(klass: KtClass, function: KtNamedFunction) {
        if (function.isOverride()) return

        val functionName = function.name
        if (functionName != null && conversionFunctionPrefix.any { functionName.startsWith(it) }) return
        if (allowOperators && function.isOperator()) return

        report(
            Finding(
                Entity.atName(function),
                "The data class ${klass.name} contains functions which are not registered " +
                    "conversion functions. The offending method is called $functionName"
            )
        )
    }
}
