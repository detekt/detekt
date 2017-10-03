package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.isNotPartOf
import io.gitlab.arturbosch.detekt.api.isPartOf
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty
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

	private val ignoreHashCodeFunction = valueOrDefault(IGNORE_HASH_CODE, false)
	private val ignoreAnnotation = valueOrDefault(IGNORE_ANNOTATION, false)
	private val ignorePropertyDeclaration = valueOrDefault(IGNORE_PROPERTY_DECLARATION, false)
	private val ignoreNamedParameters = valueOrDefault(IGNORE_NAMED_PARAMETERS, false)

	override fun visitConstantExpression(expression: KtConstantExpression) {
		if (expression.isIgnored()) {
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

	private fun KtConstantExpression.isIgnored() = when {
		ignorePropertyDeclaration && parent is KtProperty && !(parent as KtProperty).isLocal -> true
		ignoreAnnotation && this.isPartOf(KtAnnotationEntry::class) -> true
		ignoreHashCodeFunction && this.isPartOfHashCode() -> true
		ignoreNamedParameters && this.isPartOf(KtValueArgument::class) && this.isNotPartOf(KtBinaryExpression::class) -> true
		parent.isConstantProperty() -> true
		else -> false
	}

	private fun KtConstantExpression.isPartOfHashCode(): Boolean {
		val containingFunction = getNonStrictParentOfType(KtNamedFunction::class.java)
		val name = containingFunction?.name
		val returnType = containingFunction?.typeReference?.node?.text
		return nameIsHashCode(name) && returnTypeIsInt(returnType)
	}

	private fun returnTypeIsInt(returnType: String?) = returnType != null && returnType == "Int"
	private fun nameIsHashCode(name: String?) = name != null && name == "hashCode"

	private fun PsiElement.isConstantProperty(): Boolean =
			this is KtProperty && this.hasModifier(KtTokens.CONST_KEYWORD)

	private fun PsiElement.hasUnaryMinusPrefix(): Boolean = this is KtPrefixExpression
			&& (this.firstChild as? KtOperationReferenceExpression)?.operationSignTokenType == KtTokens.MINUS

	private fun parseAsDoubleOrNull(rawToken: String?): Double? = try {
		rawToken?.let { parseAsDouble(it) }
	} catch (e: NumberFormatException) {
		null
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
		const val IGNORE_NUMBERS = "ignoreNumbers"
		const val IGNORE_HASH_CODE = "ignoreHashCodeFunction"
		const val IGNORE_PROPERTY_DECLARATION = "ignorePropertyDeclaration"
		const val IGNORE_NAMED_PARAMETERS = "ignoreNamedParameters"
		const val IGNORE_ANNOTATION = "ignoreAnnotation"

		private const val HEX_RADIX = 16
	}
}
