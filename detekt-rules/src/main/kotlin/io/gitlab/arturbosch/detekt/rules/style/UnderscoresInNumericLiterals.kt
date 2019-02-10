package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import java.util.Locale
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

/**
 * This rule detects and reports decimal base 10 numeric literals above a certain length that should be underscore
 * separated for readability.
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
 * @configuration minAcceptableLength - Length under which decimal base 10 literals are not required to have underscores
 * (default: 4)
 * @configuration ignoredNames - Parameter or property names that are not to be reported on (default: "")
 *
 * @author Tyler Wong
 */
class UnderscoresInNumericLiterals(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "Report missing or invalid underscores in decimal base 10 numeric literals. Numeric literals " +
                    "should be underscore separated to increase readability. Underscores that do not make groups of " +
                    "3 digits are also reported.", Debt.FIVE_MINS)

    private val underscoreNumberRegex = Regex("^[0-9]{1,3}(_[0-9]{3})*\$")

    private val minAcceptableLength = valueOrDefault(MIN_ACCEPTABLE_LENGTH, DEFAULT_MIN_ACCEPTABLE_LENGTH_VALUE)
    private val ignoredFieldNames = valueOrDefault(IGNORED_NAMES, "")
            .splitToSequence(",")
            .filterNot { it.isEmpty() }
            .toList()

    private val KtConstantExpression.associatedName: String?
        get() {
            var associatedName: String? = null
            var element: PsiElement? = parent

            while (associatedName == null || element == null) {
                associatedName = when (element) {
                    is KtProperty -> element.name
                    is KtParameter -> element.name
                    else -> null
                }
                element = element?.parent
            }

            return associatedName
        }

    override fun visitConstantExpression(expression: KtConstantExpression) {
        if (isNameExcluded(expression) || isNotDecimal(expression)) {
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

    private fun isNotDecimal(expression: KtConstantExpression): Boolean {
        val rawText = expression.text.toLowerCase(Locale.US)
        return rawText.startsWith(HEX_PREFIX) || rawText.startsWith(BIN_PREFIX)
    }

    private fun isNameExcluded(expression: KtConstantExpression): Boolean {
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

        private const val HEX_PREFIX = "0x"
        private const val BIN_PREFIX = "0b"
        private const val DEFAULT_MIN_ACCEPTABLE_LENGTH_VALUE = 4
    }
}
