package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isOperator
import io.gitlab.arturbosch.detekt.rules.isOverride
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
class DataClassContainsFunctions(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "DataClassContainsFunctions",
        Severity.Style,
        "Data classes should mainly be used to store data and should not have any extra functions " +
            "(Compiler will automatically generate equals, toString and hashCode functions).",
        Debt.TWENTY_MINS
    )

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
        if (functionName != null && (isAllowedConversionFunction(functionName) || checkOperator(function))) return

        report(
            CodeSmell(
                issue,
                Entity.atName(function),
                "The data class ${klass.name} contains functions which are not registered " +
                    "conversion functions. The offending method is called $functionName"
            )
        )
    }

    private fun checkOperator(function: KtNamedFunction): Boolean {
        return allowOperators && function.isOperator()
    }

    private fun isAllowedConversionFunction(functionName: String): Boolean {
        return conversionFunctionPrefix.any { functionName.startsWith(it) }
    }
}
