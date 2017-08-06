package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty
import java.util.*

class MagicNumber(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Report magic numbers. Magic number is a numeric literal that is not defined as a constant " +
					"and hence it's unclear what the purpose of this number is. " +
					"It's better to declare such numbers as constants and give them a proper name. " +
					"By default, -1, 0, 1, and 2 are not considered to be magic numbers.", Debt.TEN_MINS)

	private val ignoredNumbers = valueOrDefault(IGNORED_NUMBERS, "-1,0,1,2")
			.split(",")
			.filterNot { it.isEmpty() }
			.map { parseAsDouble(it) }
			.sorted()

	override fun visitConstantExpression(expression: KtConstantExpression) {
		val parent = expression.parent

		if (parent.isConstantProperty()) {
			return
		}

		val rawNumber = if (parent.hasUnaryMinusPrefix()) {
			parent.text
		} else {
			expression.text
		}

		val number = parseAsDoubleOrNull(rawNumber) ?: return
		if (!ignoredNumbers.contains(number)) {
			report(CodeSmell(issue, Entity.from(expression)))
		}
	}

	private fun PsiElement.isConstantProperty(): Boolean {
		return this is KtProperty && this.hasModifier(KtTokens.CONST_KEYWORD)
	}

	private fun PsiElement.hasUnaryMinusPrefix(): Boolean {
		return this is KtPrefixExpression
				&& (this.firstChild as? KtOperationReferenceExpression)?.operationSignTokenType == KtTokens.MINUS
	}

	private fun parseAsDoubleOrNull(rawToken: String?): Double? {
		try {
			return rawToken?.let { parseAsDouble(it) }
		} catch (e: NumberFormatException) {
			return null
		}
	}

	private fun parseAsDouble(rawNumber: String): Double {
		val normalizedText = normalizeForParsingAsDouble(rawNumber)
		return if (normalizedText.startsWith("0x")) {
			normalizedText.removePrefix("0x")
					.toLong(HEX_RADIX)
					.toDouble()
		} else {
			normalizedText.toDouble()
		}
	}

	private fun normalizeForParsingAsDouble(text: String): String {
		return text.trim()
				.toLowerCase(Locale.US)
				.replace("_", "")
				.removeSuffix("l")
				.removeSuffix("d")
				.removeSuffix("f")
	}

	companion object {
		const val IGNORED_NUMBERS = "ignoredNumbers"

		private const val HEX_RADIX = 16
	}
}
