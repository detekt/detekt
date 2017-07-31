package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtProperty

class MagicNumber(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Report magic numbers. Magic number is a numeric literal that is not defined as a constant." +
					"By default, -1, 0, 1, and 2 are not considered to be magic numbers. ", Debt.TEN_MINS)

	private val ignoreNumbers = valueOrDefault(IGNORE_NUMBERS, "-1,0,1,2")
			.split(",")
			.map { it.toLongOrNull() }
			.filterNotNull()

	override fun visitConstantExpression(expression: KtConstantExpression) {
		val parent = expression.parent

		val isConst = parent is KtProperty && parent.modifierList?.hasModifier(KtTokens.CONST_KEYWORD) ?: false
		val possibleNumber = getNumber(expression)

		if (!isConst && !ignoreNumbers.contains(possibleNumber)) {
			report(CodeSmell(issue, Entity.from(expression)))
		}
	}

	private fun getNumber(element: KtConstantExpression): Long? {
		val text = element.text

		return when {
      text.endsWith("L") -> text.replace("L", "").toLongOrNull()
      text.startsWith("0x") -> text.substring("0x".length).toIntOrNull(HEX_RADIX)?.toLong()
      text.contains(".") -> text.toFloatOrNull()?.toLong()
      else -> text.toLongOrNull()
    }
	}

	companion object {
		const val IGNORE_NUMBERS = "ignoreNumbers"

		private const val HEX_RADIX = 16
	}
}
