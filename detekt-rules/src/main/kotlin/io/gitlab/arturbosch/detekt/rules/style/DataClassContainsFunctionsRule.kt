package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

class DataClassContainsFunctionsRule(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("DataClassContainsFunctions",
			Severity.Style,
			"Data classes should mainly be used to store data and should not have any extra functions." +
					"(Compiler will automatically generate equals, toString and hashCode functions)")

	private val visitor = FunctionsVisitor(this)

	private val excludes = Excludes(valueOrDefault(CONVERSION_FUNCTION_PREFIX, ""))

	override fun visitClass(klass: KtClass) {
		if (klass.isData()) {
			klass.getBody()?.declarations?.forEach {
				it.accept(visitor)
			}
		}
		super.visitClass(klass)
	}

	private fun handleNamedFunction(function: KtNamedFunction) {
		if (!isOverridden(function) && !isConversionFunction(function)) {
			report(CodeSmell(issue, Entity.from(function)))
		}
	}

	private fun isOverridden(function: KtNamedFunction): Boolean {
		return function.modifierList?.hasModifier(KtTokens.OVERRIDE_KEYWORD) ?: false
	}

	private fun isConversionFunction(function: KtNamedFunction): Boolean {
		return excludes.startWith(function.name)
	}

	private class FunctionsVisitor(val rule: DataClassContainsFunctionsRule) : DetektVisitor() {
		override fun visitNamedFunction(function: KtNamedFunction) {
			rule.handleNamedFunction(function)
		}
	}

	companion object {
		const val CONVERSION_FUNCTION_PREFIX = "conversionFunctionPrefix"
	}
}
