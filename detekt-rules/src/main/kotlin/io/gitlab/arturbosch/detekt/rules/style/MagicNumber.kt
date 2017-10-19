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
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import java.util.Locale

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
	private val ignoreCompanionObjectPropertyDeclaration = valueOrDefault(IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION, true)

	override fun visitConstantExpression(expression: KtConstantExpression) {

		if (isIgnoredByConfig(expression) || expression.isPartOfFunctionReturnConstant()) {
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
			report(CodeSmell(issue, Entity.from(expression)))
		}
	}

	private fun isIgnoredByConfig(expression: KtConstantExpression) = when {
		ignorePropertyDeclaration && expression.isProperty() -> true
		ignoreConstantDeclaration && expression.isConstantProperty() -> true
		ignoreCompanionObjectPropertyDeclaration && expression.isCompanionObjectProperty() -> true
		ignoreAnnotation && expression.isPartOf(KtAnnotationEntry::class) -> true
		ignoreHashCodeFunction && expression.isPartOfHashCode() -> true
		ignoreEnums && expression.isPartOf(KtEnumEntry::class) -> true
		ignoreNamedArgument
				&& expression.isPartOf(KtValueArgument::class)
				&& expression.isPartOf(KtCallExpression::class) -> true
		else -> false
	}

	private fun KtConstantExpression.isPartOfFunctionReturnConstant() =
			parent is KtNamedFunction || (parent is KtReturnExpression && parent.parent.children.size == 1)


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
