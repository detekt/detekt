package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.isOverridden
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 *
 * <noncompliant>
 * data class DataClassWithFunctions(val i: Int) {
 *     fun foo() { }
 * }
 * </noncompliant>
 *
 * <compliant>
 * data class DataClassWithFunctions(val i: Int) {
 * }
 * </compliant>
 *
 * @configuration conversionFunctionPrefix - allowed conversion function names (default: 'to')
 * @author Ivan Balaksha
 * @author schalkms
 * @author Marvin Ramin
 */
class DataClassContainsFunctions(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("DataClassContainsFunctions",
			Severity.Style,
			"Data classes should mainly be used to store data and should not have any extra functions. " +
					"(Compiler will automatically generate equals, toString and hashCode functions)")

	private val conversionFunctionPrefix = SplitPattern(valueOrDefault(CONVERSION_FUNCTION_PREFIX, ""))

	override fun visitClass(klass: KtClass) {
		if (klass.isData()) {
			klass.getBody()?.declarations
					?.filterIsInstance<KtNamedFunction>()
					?.forEach { checkFunction(klass, it) }
		}
		super.visitClass(klass)
	}

	private fun checkFunction(klass: KtClass, function: KtNamedFunction) {
		if (!function.isOverridden() && !conversionFunctionPrefix.startWith(function.name)) {
			report(CodeSmell(issue, Entity.from(function),
					"The data class ${klass.name} contains functions which are not registered " +
							"conversion functions. The offending method is called ${function.name}"))
		}
	}

	companion object {
		const val CONVERSION_FUNCTION_PREFIX = "conversionFunctionPrefix"
	}
}
