package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.rules.isConstant
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

/**
 * This rule identifies and reports properties (`val`) that may be `const val` instead.
 * Using `const val` can lead to better performance of the resulting bytecode as well as better interoperability with
 * Java.
 *
 * <noncompliant>
 * val myConstant = "abc"
 * </noncompliant>
 *
 * <compliant>
 * const val MY_CONSTANT = "abc"
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class MayBeConst(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Reports vals that can be const val instead.",
        Debt.FIVE_MINS
    )

    override val defaultRuleIdAliases = setOf("MayBeConstant")

    private val binaryTokens = hashSetOf<KtSingleValueToken>(
        KtTokens.PLUS,
        KtTokens.MINUS,
        KtTokens.MUL,
        KtTokens.DIV,
        KtTokens.PERC
    )

    private val topLevelConstants = HashSet<String?>()
    private val companionObjectConstants = HashSet<String?>()

    override fun visitKtFile(file: KtFile) {
        topLevelConstants.clear()
        val topLevelProperties = file.declarations
            .filterIsInstance<KtProperty>()
            .filter { it.isTopLevel && it.isConstant() }
            .mapNotNull { it.name }
        topLevelConstants.addAll(topLevelProperties)
        super.visitKtFile(file)
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        if (declaration.isObjectLiteral()) { // local vals can't be const
            return
        }
        val constProperties = declaration.declarations
            .filterIsInstance<KtProperty>()
            .filter { it.isConstant() }
            .mapNotNull { it.name }
        companionObjectConstants.addAll(constProperties)
        super.visitObjectDeclaration(declaration)
        companionObjectConstants.removeAll(constProperties)
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        if (property.canBeConst()) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(property),
                    "${property.nameAsSafeName} can be a `const val`."
                )
            )
        }
    }

    private fun KtProperty.canBeConst(): Boolean {
        if (cannotBeConstant() || isInObject() || isJvmField()) {
            return false
        }
        return this.initializer?.isConstantExpression() == true
    }

    private fun KtProperty.isJvmField(): Boolean {
        val isJvmField = annotationEntries.any { it.text == "@JvmField" }
        return annotationEntries.isNotEmpty() && !isJvmField
    }

    private fun KtProperty.cannotBeConstant(): Boolean {
        return isLocal ||
            isVar ||
            getter != null ||
            isConstant() ||
            isOverride()
    }

    private fun KtProperty.isInObject() =
        !isTopLevel && containingClassOrObject !is KtObjectDeclaration

    private fun KtExpression.isConstantExpression(): Boolean {
        return this is KtStringTemplateExpression && !hasInterpolation() ||
            node.elementType == KtNodeTypes.BOOLEAN_CONSTANT ||
            node.elementType == KtNodeTypes.INTEGER_CONSTANT ||
            node.elementType == KtNodeTypes.CHARACTER_CONSTANT ||
            node.elementType == KtNodeTypes.FLOAT_CONSTANT ||
            topLevelConstants.contains(text) ||
            companionObjectConstants.contains(text) ||
            isBinaryExpression(this) ||
            isParenthesizedExpression(this)
    }

    private fun isParenthesizedExpression(expression: KtExpression) =
        (expression as? KtParenthesizedExpression)?.expression?.isConstantExpression() == true

    private fun isBinaryExpression(expression: KtExpression): Boolean {
        return expression is KtBinaryExpression &&
            expression.node.elementType == KtNodeTypes.BINARY_EXPRESSION &&
            binaryTokens.contains(expression.operationToken) &&
            expression.left?.isConstantExpression() == true &&
            expression.right?.isConstantExpression() == true
    }
}
