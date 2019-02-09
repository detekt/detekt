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

/**
 * This rule detects and reports base 10 numeric literals above a certain length that should be underscore separated for
 * readability.
 *
 * <noncompliant>
 * object Money {
 *     const val DEFAULT_AMOUNT = 1000000
 * }
 * </noncompliant>
 *
 * <compliant>
 * object Money {
 *     const val DEFAULT_AMOUNT = 1_000_000
 * }
 * </compliant>
 *
 * @configuration minAcceptableLength - Length under which base 10 literals are not required to have underscores
 * (default: 4)
 *
 * @author Tyler Wong
 */
class UnderscoresInNumericLiterals(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Report missing underscores in numeric literals. Numeric literals should be underscore " +
					"separated for increase readability. Underscores that do not make groups of 3 digits are also " +
					"reported. Currently, only base 10 numeric literals are supported", Debt.FIVE_MINS)

	private val underscoreNumberRegex = Regex("^[0-9]{1,3}(_[0-9]{3})*\$")

	private val minAcceptableLength = valueOrDefault(MIN_ACCEPTABLE_LENGTH, DEFAULT_MIN_ACCEPTABLE_LENGTH_VALUE)

	override fun visitConstantExpression(expression: KtConstantExpression) {
		val parent = expression.parent

		val numberString = if (parent.hasUnaryMinusPrefix()) {
			parent.text
		} else {
			expression.text
		}

		if (numberString.replace("_", "").length < minAcceptableLength) {
			return
		}

		if (!numberString.matches(underscoreNumberRegex)) {
			report(CodeSmell(issue, Entity.from(expression), "This numeric literal should be underscore " +
					"separated for readability."))
		}
	}

	private fun PsiElement.hasUnaryMinusPrefix(): Boolean = this is KtPrefixExpression &&
			(this.firstChild as? KtOperationReferenceExpression)?.operationSignTokenType == KtTokens.MINUS

	companion object {
		const val MIN_ACCEPTABLE_LENGTH = "minAcceptableLength"

		private const val DEFAULT_MIN_ACCEPTABLE_LENGTH_VALUE = 4
	}
}
