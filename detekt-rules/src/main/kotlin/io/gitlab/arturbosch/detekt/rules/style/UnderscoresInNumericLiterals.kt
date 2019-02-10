package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import java.util.Locale

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
 * @configuration ignoredNames - Names that are not to be reported on (default: "")
 *
 * @author Tyler Wong
 */
class UnderscoresInNumericLiterals(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Report missing or invalid underscores in base 10 numeric literals. Numeric literals should " +
					"be underscore separated to increase readability. Underscores that do not make groups of 3 " +
					"digits are also reported.", Debt.FIVE_MINS)

	private val underscoreNumberRegex = Regex("^[0-9]{1,3}(_[0-9]{3})*\$")

	private val minAcceptableLength = valueOrDefault(MIN_ACCEPTABLE_LENGTH, DEFAULT_MIN_ACCEPTABLE_LENGTH_VALUE)
	private val ignoredFieldNames = valueOrDefault(IGNORED_NAMES, "")
			.splitToSequence(",")
			.filterNot { it.isEmpty() }
			.toList()

	private val KtConstantExpression.associatedName: String?
		get() {
			var propertyName: String? = null
			var element: PsiElement? = parent

			while (propertyName == null || element == null) {
				propertyName = when (element) {
					is KtProperty -> element.name
					is KtParameter -> element.name
					else -> null
				}
				element = element?.parent
			}

			return propertyName
		}

	override fun visitConstantExpression(expression: KtConstantExpression) {
		if (propertyNameIsExcluded(expression)) {
			return
		}

		val numberStringParts = normalizeForMatching(expression.text).split('.')

		if (numberStringParts.sumBy { it.length } < minAcceptableLength) {
			if (numberStringParts.any { it.contains('_') }) {
				reportIfInvalid(expression, numberStringParts)
			}
			return
		}

		reportIfInvalid(expression, numberStringParts)
	}

	private fun reportIfInvalid(expression: KtConstantExpression, numberStringParts: List<String>) {
		for (part in numberStringParts) {
			if (!part.matches(underscoreNumberRegex)) {
				report(CodeSmell(issue, Entity.from(expression), "This numeric literal should be separated " +
						"by underscores in order to increase readability."))
				break
			}
		}
	}

	private fun propertyNameIsExcluded(expression: KtConstantExpression): Boolean {
		val propertyName = expression.associatedName
		return ignoredFieldNames.contains(propertyName)
	}

	private fun normalizeForMatching(text: String): String {
		return text.trim()
				.toLowerCase(Locale.US)
				.removeSuffix("l")
				.removeSuffix("d")
				.removeSuffix("f")
	}

	companion object {
		const val MIN_ACCEPTABLE_LENGTH = "minAcceptableLength"
		const val IGNORED_NAMES = "ignoredNames"

		private const val DEFAULT_MIN_ACCEPTABLE_LENGTH_VALUE = 4
	}
}
