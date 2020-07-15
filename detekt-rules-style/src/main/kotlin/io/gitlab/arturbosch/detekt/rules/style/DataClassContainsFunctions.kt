package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
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
 *
 * @configuration conversionFunctionPrefix - allowed conversion function names (default: `'to'`)
 */
class DataClassContainsFunctions(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue("DataClassContainsFunctions",
            Severity.Style,
            "Data classes should mainly be used to store data and should not have any extra functions. " +
                    "(Compiler will automatically generate equals, toString and hashCode functions)",
            Debt.TWENTY_MINS)

    private val conversionFunctionPrefix = SplitPattern(valueOrDefault(CONVERSION_FUNCTION_PREFIX, ""))

    override fun visitClass(klass: KtClass) {
        if (klass.isData()) {
            klass.body?.declarations
                    ?.filterIsInstance<KtNamedFunction>()
                    ?.forEach { checkFunction(klass, it) }
        }
        super.visitClass(klass)
    }

    private fun checkFunction(klass: KtClass, function: KtNamedFunction) {
        if (!function.isOverride() && !conversionFunctionPrefix.startWith(function.name)) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    "The data class ${klass.name} contains functions which are not registered " +
                        "conversion functions. The offending method is called ${function.name}"
                )
            )
        }
    }

    companion object {
        const val CONVERSION_FUNCTION_PREFIX = "conversionFunctionPrefix"
    }
}
