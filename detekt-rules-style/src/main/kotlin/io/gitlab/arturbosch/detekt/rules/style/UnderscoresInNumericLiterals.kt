package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import java.util.Locale

/**
 * This rule detects and reports long base 10 numbers which should be separated with underscores
 * for readability. For `Serializable` classes or objects, the field `serialVersionUID` is
 * explicitly ignored. For floats and doubles, anything to the right of the decimal point is ignored.
 *
 * <noncompliant>
 * const val DEFAULT_AMOUNT = 1000000
 * </noncompliant>
 *
 * <compliant>
 * const val DEFAULT_AMOUNT = 1_000_000
 * </compliant>
 */
class UnderscoresInNumericLiterals(config: Config) : Rule(
    config,
    "Report missing or invalid underscores in base 10 numbers. Numeric literals " +
        "should be underscore separated to increase readability."
) {

    @Configuration("Maximum number of consecutive digits that a numeric literal can have without using an underscore")
    private val acceptableLength: Int by config(4)

    @Configuration("If set to false, groups of exactly three digits must be used. If set to true, 100_00 is allowed.")
    private val allowNonStandardGrouping: Boolean by config(false)

    private val nonCompliantRegex: Regex = """\d{${acceptableLength + 1},}""".toRegex()

    override fun visitConstantExpression(expression: KtConstantExpression) {
        val normalizedText = normalizeForMatching(expression.text)
        checkNormalized(normalizedText, expression)
    }

    private fun checkNormalized(normalizedText: String, expression: KtConstantExpression) {
        if (isNotDecimalNumber(normalizedText)) {
            return
        }
        if (expression.isSerialUidProperty()) {
            return
        }

        val numberString = normalizedText.split('.').first()

        if (!allowNonStandardGrouping && numberString.hasNonStandardGrouping()) {
            return doReport(expression, "The number contains a non standard grouping.")
        }

        if (numberString.contains(nonCompliantRegex)) {
            return doReport(
                expression,
                "This number should be separated by underscores in order to increase readability."
            )
        }
    }

    private fun isNotDecimalNumber(rawText: String): Boolean =
        rawText.replace("_", "").toDoubleOrNull() == null ||
            rawText.startsWith(HEX_PREFIX) ||
            rawText.startsWith(BIN_PREFIX)

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

    private fun normalizeForMatching(text: String): String =
        text.trim()
            .lowercase(Locale.ROOT)
            .removeSuffix("l")
            .removeSuffix("d")
            .removeSuffix("f")

    private fun doReport(expression: KtConstantExpression, message: String) {
        report(CodeSmell(Entity.from(expression), message))
    }

    private fun String.hasNonStandardGrouping(): Boolean = contains('_') && !matches(HAS_ONLY_STANDARD_GROUPING)

    companion object {
        private val HAS_ONLY_STANDARD_GROUPING = """\d{1,3}(?:_\d{3})*""".toRegex()
        private const val HEX_PREFIX = "0x"
        private const val BIN_PREFIX = "0b"
        private const val SERIALIZABLE = "Serializable"
        private const val SERIAL_UID_PROPERTY_NAME = "serialVersionUID"
    }
}
