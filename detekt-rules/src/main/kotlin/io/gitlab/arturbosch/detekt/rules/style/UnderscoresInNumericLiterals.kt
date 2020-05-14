package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import java.util.Locale

/**
 * This rule detects and reports decimal base 10 numeric literals above a certain length that should be underscore
 * separated for readability. Underscores that do not make groups of 3 digits are also reported even if their length is
 * under the `acceptableDecimalLength`. For `Serializable` classes or objects, the field `serialVersionUID` is
 * explicitly ignored. For floats and doubles, anything to the right of the decimal is ignored.
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
 * @configuration acceptableDecimalLength - Length under which decimal base 10 literals are not required to have
 * underscores (default: `5`)
 */
class UnderscoresInNumericLiterals(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "Report missing or invalid underscores in decimal base 10 numeric literals. Numeric literals " +
                    "should be underscore separated to increase readability. Underscores that do not make groups of " +
                    "3 digits are also reported.", Debt.FIVE_MINS)

    private val underscoreNumberRegex = Regex("[0-9]{1,3}(_[0-9]{3})*")

    private val acceptableDecimalLength = valueOrDefault(ACCEPTABLE_DECIMAL_LENGTH, DEFAULT_ACCEPTABLE_DECIMAL_LENGTH)

    override fun visitConstantExpression(expression: KtConstantExpression) {
        val normalizedText = normalizeForMatching(expression.text)

        if (isNotDecimalNumber(normalizedText) || expression.isSerialUidProperty()) {
            return
        }

        val numberString = normalizedText.split('.').first()

        if (numberString.length >= acceptableDecimalLength || numberString.contains('_')) {
            reportIfInvalidUnderscorePattern(expression, numberString)
        }
    }

    private fun reportIfInvalidUnderscorePattern(expression: KtConstantExpression, numberString: String) {
        if (!numberString.matches(underscoreNumberRegex)) {
            report(CodeSmell(issue, Entity.from(expression), "This numeric literal should be separated " +
                    "by underscores in order to increase readability."))
        }
    }

    private fun isNotDecimalNumber(rawText: String): Boolean {
        return rawText.replace("_", "").toDoubleOrNull() == null || rawText.startsWith(HEX_PREFIX) ||
                rawText.startsWith(BIN_PREFIX)
    }

    private fun KtConstantExpression.isSerialUidProperty(): Boolean {
        val propertyElement = if (parent is KtPrefixExpression) parent?.parent else parent
        val property = propertyElement as? KtProperty
        return property != null && property.name == SERIAL_UID_PROPERTY_NAME && isSerializable(property)
    }

    private fun isSerializable(property: KtProperty): Boolean {
        var containingClassOrObject = property.containingClassOrObject
        if (containingClassOrObject is KtObjectDeclaration && containingClassOrObject.isCompanion()) {
            containingClassOrObject = containingClassOrObject.containingClassOrObject
        }
        return containingClassOrObject
            ?.superTypeListEntries
            ?.any { it.text == SERIALIZABLE } == true
    }

    private fun normalizeForMatching(text: String): String {
        return text.trim()
                .toLowerCase(Locale.US)
                .removeSuffix("l")
                .removeSuffix("d")
                .removeSuffix("f")
    }

    companion object {
        const val ACCEPTABLE_DECIMAL_LENGTH = "acceptableDecimalLength"

        private const val HEX_PREFIX = "0x"
        private const val BIN_PREFIX = "0b"
        private const val SERIALIZABLE = "Serializable"
        private const val SERIAL_UID_PROPERTY_NAME = "serialVersionUID"
        private const val DEFAULT_ACCEPTABLE_DECIMAL_LENGTH = 5
    }
}
