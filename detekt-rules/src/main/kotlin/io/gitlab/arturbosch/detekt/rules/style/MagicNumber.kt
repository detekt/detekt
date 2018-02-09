package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.isPartOf
import io.gitlab.arturbosch.detekt.rules.isHashCodeFunction
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import java.util.Locale

/**
 * This rule detects and reports usages of magic numbers in the code. Prefer defining constants with clear names
 * describing what the magic number means.
 *
 * <noncompliant>
 * class User {
 *
 *     fun checkName(name: String) {
 *         if (name.length > 42) {
 *             throw IllegalArgumentException("username is too long")
 *         }
 *         // ...
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 *
 * class User {
 *
 *     fun checkName(name: String) {
 *         if (name.length > MAX_USERNAME_SIZE) {
 *             throw IllegalArgumentException("username is too long")
 *         }
 *         // ...
 *     }
 *
 *     companion object {
 *         private const val MAX_USERNAME_SIZE = 42
 *     }
 * }
 * </compliant>
 *
 * @configuration ignoreNumbers - numbers which do not count as magic numbers (default: '-1,0,1,2')
 * @configuration ignoreHashCodeFunction - whether magic numbers in hashCode functions should be ignored
 * (default: false)
 * @configuration ignorePropertyDeclaration - whether magic numbers in property declarations should be ignored
 * (default: false)
 * @configuration ignoreConstantDeclaration - whether magic numbers in property declarations should be ignored
 * (default: true)
 * @configuration ignoreCompanionObjectPropertyDeclaration - whether magic numbers in companion object
 * declarations should be ignored (default: true)
 * @configuration ignoreAnnotation - whether magic numbers in annotations should be ignored
 * (default: false)
 * @configuration ignoreNamedArgument - whether magic numbers in named arguments should be ignored
 * (default: true)
 * @configuration ignoreEnums - whether magic numbers in enums should be ignored (default: false)
 *
 * @active since v1.0.0
 * @author Marvin Ramin
 */
class MagicNumber(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Report magic numbers. Magic number is a numeric literal that is not defined as a constant " +
					"and hence it's unclear what the purpose of this number is. " +
					"It's better to declare such numbers as constants and give them a proper name. " +
					"By default, -1, 0, 1, and 2 are not considered to be magic numbers.", Debt.TEN_MINS)

	private val ignoredNumbers = valueOrDefault(IGNORE_NUMBERS, "-1,0,1,2")
			.split(",")
			.filterNot { it.isEmpty() }
			.map { parseAsDouble(it) }
			.sorted()

	private val ignoreAnnotation = valueOrDefault(IGNORE_ANNOTATION, false)
	private val ignoreHashCodeFunction = valueOrDefault(IGNORE_HASH_CODE, false)
	private val ignorePropertyDeclaration = valueOrDefault(IGNORE_PROPERTY_DECLARATION, false)
	private val ignoreNamedArgument = valueOrDefault(IGNORE_NAMED_ARGUMENT, false)
	private val ignoreEnums = valueOrDefault(IGNORE_ENUMS, false)
	private val ignoreConstantDeclaration = valueOrDefault(IGNORE_CONSTANT_DECLARATION, true)
	private val ignoreCompanionObjectPropertyDeclaration =
			valueOrDefault(IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION, true)

	override fun visitConstantExpression(expression: KtConstantExpression) {
		if (isIgnoredByConfig(expression) || expression.isPartOfFunctionReturnConstant()
				|| expression.isPartOfConstructor()) {
			return
		}

		val parent = expression.parent
		val rawNumber = if (parent.hasUnaryMinusPrefix()) {
			parent.text
		} else {
			expression.text
		}

		val number = parseAsDoubleOrNull(rawNumber) ?: return
		if (!ignoredNumbers.contains(number)) {
			report(CodeSmell(issue, Entity.from(expression), "This expression contains a magic number." +
					" Consider defining it to a well named constant."))
		}
	}

	private fun isIgnoredByConfig(expression: KtConstantExpression) = when {
		ignorePropertyDeclaration && expression.isProperty() -> true
		ignoreConstantDeclaration && expression.isConstantProperty() -> true
		ignoreCompanionObjectPropertyDeclaration && expression.isCompanionObjectProperty() -> true
		ignoreAnnotation && expression.isPartOf(KtAnnotationEntry::class) -> true
		ignoreHashCodeFunction && expression.isPartOfHashCode() -> true
		ignoreEnums && expression.isPartOf(KtEnumEntry::class) -> true
		ignoreNamedArgument && expression.isNamedArgument() -> true
		else -> false
	}

	private fun parseAsDoubleOrNull(rawToken: String?): Double? = try {
		rawToken?.let { parseAsDouble(it) }
	} catch (e: NumberFormatException) {
		null
	}

	private fun parseAsDouble(rawNumber: String): Double {
		val normalizedText = normalizeForParsingAsDouble(rawNumber)
		return when {
			normalizedText.startsWith("0x") || normalizedText.startsWith("0X") ->
				normalizedText.substring(2).toLong(HEX_RADIX).toDouble()
			normalizedText.startsWith("0b") || normalizedText.startsWith("0B") ->
				normalizedText.substring(2).toLong(BINARY_RADIX).toDouble()
			else -> normalizedText.toDouble()
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
		const val IGNORE_NUMBERS = "ignoreNumbers"
		const val IGNORE_HASH_CODE = "ignoreHashCodeFunction"
		const val IGNORE_PROPERTY_DECLARATION = "ignorePropertyDeclaration"
		const val IGNORE_CONSTANT_DECLARATION = "ignoreConstantDeclaration"
		const val IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION = "ignoreCompanionObjectPropertyDeclaration"
		const val IGNORE_ANNOTATION = "ignoreAnnotation"
		const val IGNORE_NAMED_ARGUMENT = "ignoreNamedArgument"
		const val IGNORE_ENUMS = "ignoreEnums"

		private const val HEX_RADIX = 16
		private const val BINARY_RADIX = 2
	}
}

private fun KtConstantExpression.isNamedArgument() =
		(parent is KtValueArgument
				&& (parent as? KtValueArgument)?.isNamed() == true
				&& isPartOf(KtCallExpression::class))

private fun KtConstantExpression.isPartOfFunctionReturnConstant() =
		parent is KtNamedFunction || (parent is KtReturnExpression && parent.parent.children.size == 1)

private fun KtConstantExpression.isPartOfConstructor(): Boolean {
	return parent is KtParameter
			&& parent.parent.parent is KtPrimaryConstructor || parent.parent.parent is KtSecondaryConstructor
}

private fun KtConstantExpression.isPartOfHashCode(): Boolean {
	val containingFunction = getNonStrictParentOfType(KtNamedFunction::class.java)
	return containingFunction?.isHashCodeFunction() == true
}

private fun KtConstantExpression.isProperty() =
		getNonStrictParentOfType(KtProperty::class.java)?.let { !it.isLocal } ?: false

private fun KtConstantExpression.isCompanionObjectProperty() = isProperty() && isInCompanionObject()

private fun KtConstantExpression.isInCompanionObject() =
		getNonStrictParentOfType(KtObjectDeclaration::class.java)?.isCompanion() ?: false

private fun KtConstantExpression.isConstantProperty(): Boolean =
		isProperty() && getNonStrictParentOfType(KtProperty::class.java)?.hasModifier(KtTokens.CONST_KEYWORD) ?: false

private fun PsiElement.hasUnaryMinusPrefix(): Boolean = this is KtPrefixExpression
		&& (this.firstChild as? KtOperationReferenceExpression)?.operationSignTokenType == KtTokens.MINUS
