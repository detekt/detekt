package io.gitlab.arturbosch.detekt.rules.style

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.parsing.KotlinExpressionParsing.Precedence
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtPsiUtil

/**
 * This rule reports unnecessary parentheses around expressions.
 * These unnecessary parentheses can safely be removed.
 *
 * Added in v1.0.0.RC4
 *
 * <noncompliant>
 * val local = (5 + 3)
 *
 * if ((local == 8)) { }
 *
 * fun foo() {
 *     function({ input -> println(input) })
 * }
 * </noncompliant>
 *
 * <compliant>
 * val local = 5 + 3
 *
 * if (local == 8) { }
 *
 * fun foo() {
 *     function { input -> println(input) }
 * }
 * </compliant>
 */
class UnnecessaryParentheses(config: Config) : Rule(
    config,
    "Unnecessary parentheses don't add any value to the code and should be removed."
) {

    @Configuration(
        "allow parentheses when not strictly required but precedence may be unclear, such as `(a && b) || c`"
    )
    private val allowForUnclearPrecedence: Boolean by config(defaultValue = false)

    override fun visitParenthesizedExpression(expression: KtParenthesizedExpression) {
        super.visitParenthesizedExpression(expression)

        val inner = expression.expression ?: return

        if (expression.parent is KtDelegatedSuperTypeEntry) return

        if (!KtPsiUtil.areParenthesesUseless(expression)) return

        if (allowForUnclearPrecedence && inner.isBinaryOperationPrecedenceUnclearWithParent()) return

        if (allowForUnclearPrecedence && expression.isUnaryOperationPrecedenceUnclear()) return

        if (allowForUnclearPrecedence && expression.isFloatWithOutIntegerPartAroundRange()) return

        val message = "Parentheses in ${expression.text} are unnecessary and can be replaced with: " +
            KtPsiUtil.deparenthesize(expression)?.text
        report(Finding(Entity.from(expression), message))
    }

    companion object {
        /**
         * Map from operators to a set of other operators between which precedence can be unclear.
         *
         * This is built from a mapping of [Precedence] to other, greater, [Precedence](s) which should be considered
         * unclear when mixed as child binary expressions.
         */
        @Suppress("CommentOverPrivateProperty")
        private val childToUnclearPrecedenceParentsMapping: Map<IElementType, Set<IElementType>> = arrayOf(
            Precedence.ELVIS to arrayOf(
                Precedence.EQUALITY, // (a ?: b) == c
                Precedence.COMPARISON, // (a ?: b) > c
                Precedence.IN_OR_IS, // (a ?: b) in c
            ),
            Precedence.SIMPLE_NAME to arrayOf(
                Precedence.ELVIS, // a ?: (b to c)
                Precedence.SIMPLE_NAME, // (a to b) to c
            ),
            Precedence.MULTIPLICATIVE to arrayOf(
                Precedence.ADDITIVE, // (a * b) + c
                Precedence.RANGE, // (a / b)..(c * d)
                // taken from https://github.com/JetBrains/intellij-kotlin/commit/70cd07bcffe701da0fd8c803abceef2b5c67ab9c
                Precedence.ELVIS, // a ?: (b * c)
            ),
            // (a + b)..(c + d)
            Precedence.ADDITIVE to arrayOf(
                Precedence.RANGE,
                // taken from https://github.com/JetBrains/intellij-kotlin/commit/70cd07bcffe701da0fd8c803abceef2b5c67ab9c
                Precedence.ELVIS // a ?: (b + c)
            ),
            // (a && b) || c
            Precedence.CONJUNCTION to arrayOf(Precedence.DISJUNCTION),
        )
            .onEach { (child, parents) ->
                parents.forEach { check(child <= it) }
            }
            .flatMap { (child, parents) ->
                child.operations.types.map { childOp ->
                    childOp to parents.flatMapTo(mutableSetOf()) { parentOp -> parentOp.operations.types.toList() }
                }
            }
            .toMap()

        /**
         * Retrieves the [IElementType] of the binary operation from this element if it is a non-assignment binary
         * expression, or null otherwise.
         */
        private fun PsiElement.binaryOp(): IElementType? =
            when (this) {
                is KtBinaryExpression ->
                    operationReference.takeUnless { operationToken in KtTokens.ALL_ASSIGNMENTS }
                is KtBinaryExpressionWithTypeRHS -> operationReference
                is KtIsExpression -> operationReference
                else -> null
            }?.getReferencedNameElementType()

        /**
         * Returns either the parent of this [KtExpression] or its first parent expression which is not a
         * [KtParenthesizedExpression].
         */
        private fun KtExpression.firstNonParenParent(): PsiElement? =
            generateSequence(parent) { (it as? KtParenthesizedExpression)?.parent }
                .firstOrNull { it !is KtParenthesizedExpression }

        /**
         * Determines whether this is a binary expression whose operation precedence is unclear with the parent binary
         * operation per [childToUnclearPrecedenceParentsMapping].
         */
        private fun KtExpression.isBinaryOperationPrecedenceUnclearWithParent(): Boolean {
            val innerOp = binaryOp() ?: return false
            val outerOp = firstNonParenParent()?.binaryOp() ?: return false

            return childToUnclearPrecedenceParentsMapping[innerOp]?.contains(outerOp) == true
        }

        /**
         * Determines whether this is unary operation whose precedence is unclear
         */
        @Suppress("ReturnCount")
        private fun KtParenthesizedExpression.isUnaryOperationPrecedenceUnclear(): Boolean {
            val parentExpression = this.parent
            if (parentExpression !is KtPrefixExpression) return false

            if (parentExpression.operationReference.getReferencedNameElementType() in listOf(
                    KtTokens.PLUSPLUS,
                    KtTokens.MINUSMINUS,
                ) &&
                (this.expression as? KtDotQualifiedExpression)?.receiverExpression is KtConstantExpression
            ) {
                return false
            }

            return parentExpression.operationReference.getReferencedNameElementType() in listOf(
                KtTokens.PLUS,
                KtTokens.MINUS,
                KtTokens.EXCL,
                KtTokens.PLUSPLUS,
                KtTokens.MINUSMINUS,
            )
        }

        /**
         * Determines whether this is decimal without integer part present in ranges
         */
        @Suppress("ReturnCount")
        private fun KtParenthesizedExpression.isFloatWithOutIntegerPartAroundRange(): Boolean {
            val parentExpression = this.parent
            if (parentExpression !is KtBinaryExpression) return false

            if (
                parentExpression.operationToken != KtTokens.RANGE ||
                (this.expression as? KtConstantExpression)?.elementType != KtNodeTypes.FLOAT_CONSTANT ||
                parentExpression.right != this
            ) {
                return false
            }

            return this.expression?.run { text.startsWith(".") } == true
        }
    }
}
