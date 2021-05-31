package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.config
import io.gitlab.arturbosch.detekt.rules.isConstant
import io.gitlab.arturbosch.detekt.rules.isHashCodeFunction
import io.gitlab.arturbosch.detekt.rules.isPartOf
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
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
 */
@Suppress("TooManyFunctions")
@ActiveByDefault(since = "1.0.0")
class MagicNumber(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Report magic numbers. Magic number is a numeric literal that is not defined as a constant " +
            "and hence it's unclear what the purpose of this number is. " +
            "It's better to declare such numbers as constants and give them a proper name. " +
            "By default, -1, 0, 1, and 2 are not considered to be magic numbers.",
        Debt.TEN_MINS
    )

    @Configuration("numbers which do not count as magic numbers")
    private val ignoreNumbers: List<Double> by config(listOf("-1", "0", "1", "2")) { numbers ->
        numbers.map(this::parseAsDouble).sorted()
    }

    @Configuration("whether magic numbers in hashCode functions should be ignored")
    private val ignoreHashCodeFunction: Boolean by config(true)

    @Configuration("whether magic numbers in property declarations should be ignored")
    private val ignorePropertyDeclaration: Boolean by config(false)

    @Configuration("whether magic numbers in local variable declarations should be ignored")
    private val ignoreLocalVariableDeclaration: Boolean by config(false)

    @Configuration("whether magic numbers in constant declarations should be ignored")
    private val ignoreConstantDeclaration: Boolean by config(true)

    @Configuration("whether magic numbers in companion object declarations should be ignored")
    private val ignoreCompanionObjectPropertyDeclaration: Boolean by config(true)

    @Configuration("whether magic numbers in annotations should be ignored")
    private val ignoreAnnotation: Boolean by config(false)

    @Configuration("whether magic numbers in named arguments should be ignored")
    private val ignoreNamedArgument: Boolean by config(true)

    @Configuration("whether magic numbers in enums should be ignored")
    private val ignoreEnums: Boolean by config(false)

    @Configuration("whether magic numbers in ranges should be ignored")
    private val ignoreRanges: Boolean by config(false)

    @Configuration("whether magic numbers as subject of an extension function should be ignored")
    private val ignoreExtensionFunctions: Boolean by config(true)

    override fun visitConstantExpression(expression: KtConstantExpression) {
        val elementType = expression.elementType
        if (elementType != KtNodeTypes.INTEGER_CONSTANT && elementType != KtNodeTypes.FLOAT_CONSTANT) return

        if (isIgnoredByConfig(expression) || expression.isPartOfFunctionReturnConstant() ||
            expression.isPartOfConstructorOrFunctionConstant()
        ) {
            return
        }

        val parent = expression.parent
        val rawNumber = if (parent.hasUnaryMinusPrefix()) {
            parent.text
        } else {
            expression.text
        }

        val number = parseAsDoubleOrNull(rawNumber)
        if (number != null && !ignoreNumbers.contains(number)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "This expression contains a magic number." +
                        " Consider defining it to a well named constant."
                )
            )
        }
    }

    private fun isIgnoredByConfig(expression: KtConstantExpression) = when {
        ignorePropertyDeclaration && expression.isProperty() -> true
        ignoreLocalVariableDeclaration && expression.isLocalProperty() -> true
        ignoreConstantDeclaration && expression.isConstantProperty() -> true
        ignoreCompanionObjectPropertyDeclaration && expression.isCompanionObjectProperty() -> true
        ignoreAnnotation && expression.isPartOf<KtAnnotationEntry>() -> true
        ignoreHashCodeFunction && expression.isPartOfHashCode() -> true
        ignoreEnums && expression.isPartOf<KtEnumEntry>() -> true
        ignoreNamedArgument && expression.isNamedArgument() -> true
        ignoreRanges && expression.isPartOfRange() -> true
        ignoreExtensionFunctions && expression.isSubjectOfExtensionFunction() -> true
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
            .lowercase(Locale.US)
            .replace("_", "")
            .removeSuffix("l")
            .removeSuffix("d")
            .removeSuffix("f")
    }

    private fun KtConstantExpression.isNamedArgument(): Boolean {
        /**
         * The information we need is in the enclosing [KtValueArgument]. When the number being evaluated is
         * negative, there will be an [KtPrefixExpression] in between the receiver and the [KtValueArgument].
         */
        val valueArgument = when (parent) {
            is KtPrefixExpression -> parent.parent
            else -> parent
        } as? KtValueArgument

        return valueArgument?.isNamed() == true && isPartOf<KtCallElement>()
    }

    private fun KtConstantExpression.isPartOfFunctionReturnConstant() =
        parent is KtNamedFunction || parent is KtReturnExpression && parent.parent.children.size == 1

    private fun KtConstantExpression.isPartOfConstructorOrFunctionConstant(): Boolean {
        return parent is KtParameter &&
            when (parent.parent.parent) {
                is KtNamedFunction, is KtPrimaryConstructor, is KtSecondaryConstructor -> true
                else -> false
            }
    }

    private fun KtConstantExpression.isPartOfRange(): Boolean {
        val theParent = parent
        val rangeOperators = setOf("downTo", "until", "step")
        return if (theParent is KtBinaryExpression) {
            theParent.operationToken == KtTokens.RANGE ||
                theParent.operationReference.getReferencedName() in rangeOperators
        } else {
            false
        }
    }

    private fun KtConstantExpression.isSubjectOfExtensionFunction(): Boolean {
        return parent is KtDotQualifiedExpression
    }

    private fun KtConstantExpression.isPartOfHashCode(): Boolean {
        val containingFunction = getNonStrictParentOfType<KtNamedFunction>()
        return containingFunction?.isHashCodeFunction() == true
    }

    private fun KtConstantExpression.isLocalProperty() =
        getNonStrictParentOfType<KtProperty>()?.isLocal ?: false

    private fun KtConstantExpression.isProperty() =
        getNonStrictParentOfType<KtProperty>()?.let { !it.isLocal } ?: false

    private fun KtConstantExpression.isCompanionObjectProperty() = isProperty() && isInCompanionObject()

    private fun KtConstantExpression.isInCompanionObject() =
        getNonStrictParentOfType<KtObjectDeclaration>()?.isCompanion() ?: false

    private fun KtConstantExpression.isConstantProperty(): Boolean =
        isProperty() && getNonStrictParentOfType<KtProperty>()?.isConstant() ?: false

    private fun PsiElement.hasUnaryMinusPrefix(): Boolean = this is KtPrefixExpression &&
        (firstChild as? KtOperationReferenceExpression)?.operationSignTokenType == KtTokens.MINUS

    companion object {
        private const val HEX_RADIX = 16
        private const val BINARY_RADIX = 2
    }
}
