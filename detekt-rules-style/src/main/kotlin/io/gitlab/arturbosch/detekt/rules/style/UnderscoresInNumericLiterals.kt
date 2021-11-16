package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithFallback
import io.gitlab.arturbosch.detekt.api.internal.Configuration
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
 * const val DEFAULT_AMOUNT = 10_000_00
 * </compliant>
 */
class UnderscoresInNumericLiterals(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Report missing or invalid underscores in base 10 numbers. Numeric literals " +
            "should be underscore separated to increase readability. ",
        Debt.FIVE_MINS
    )

    @Configuration("Length under which base 10 numbers are not required to have underscores")
    @Deprecated("Use `acceptableLength` instead")
    private val acceptableDecimalLength: Int by config(5) { it - 1 }

    @Suppress("DEPRECATION")
    @OptIn(UnstableApi::class)
    @Configuration("Maximum number of consecutive digits that a number can have without using an underscore")
    private val acceptableLength: Int by configWithFallback(::acceptableDecimalLength, 4)

    private val nonCompliantRegex: Regex = "\\d{${acceptableLength + 1},}".toRegex()

    override fun visitConstantExpression(expression: KtConstantExpression) {
        val normalizedText = normalizeForMatching(expression.text)

        if (isNotDecimalNumber(normalizedText) || expression.isSerialUidProperty()) {
            return
        }

        val numberString = normalizedText.split('.').first()

        if (numberString.contains(nonCompliantRegex)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "This number should be separated by underscores in order to increase readability."
                )
            )
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
            .toLowerCase(Locale.ROOT)
            .removeSuffix("l")
            .removeSuffix("d")
            .removeSuffix("f")
    }

    companion object {
        private const val HEX_PREFIX = "0x"
        private const val BIN_PREFIX = "0b"
        private const val SERIALIZABLE = "Serializable"
        private const val SERIAL_UID_PROPERTY_NAME = "serialVersionUID"
    }
}
