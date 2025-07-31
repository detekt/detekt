package io.github.detekt.metrics

import com.intellij.openapi.util.Key
import com.intellij.psi.tree.IElementType
import dev.detekt.api.DetektVisitor
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

/**
 * Kotlin implementation of the cognitive complexity metric.
 *
 * Please see the "Cognitive Complexity: A new way of measuring understandability" white paper
 * by G. Ann Campbell of SonarSource for further detail.
 *
 * https://www.sonarsource.com/docs/CognitiveComplexity.pdf
 */
class CognitiveComplexity private constructor() : DetektVisitor() {

    private var complexity: Int = 0

    override fun visitNamedFunction(function: KtNamedFunction) {
        val visitor = FunctionComplexity(function)
        visitor.visitNamedFunction(function)
        complexity += visitor.complexity
    }

    data class BinExprHolder(val expr: KtBinaryExpression, val op: IElementType, val isEnclosed: Boolean)

    @Suppress("detekt.TooManyFunctions") // visitor pattern
    class FunctionComplexity(private val givenFunction: KtNamedFunction) : DetektVisitor() {
        internal var complexity: Int = 0

        private var nesting: Int = 0

        private var topMostBinExpr: KtBinaryExpression? = null

        private fun addComplexity() {
            complexity += 1 + nesting
        }

        private inline fun nestAround(block: () -> Unit) {
            nesting++
            block()
            nesting--
        }

        private fun testJumpWithLabel(expression: KtExpressionWithLabel) {
            if (expression.labelQualifier != null) {
                complexity++
            }
        }

        private fun KtCallExpression.isRecursion(): Boolean {
            val args = lambdaArguments.size + valueArguments.size
            val isInsideSameScope = parent !is KtQualifiedExpression ||
                (parent as? KtQualifiedExpression)?.receiverExpression is KtThisExpression
            return isInsideSameScope &&
                getCallNameExpression()?.getReferencedName() == givenFunction.name &&
                args == givenFunction.valueParameters.size
        }

        override fun visitWhenExpression(expression: KtWhenExpression) {
            addComplexity()
            nestAround { super.visitWhenExpression(expression) }
        }

        override fun visitForExpression(expression: KtForExpression) {
            addComplexity()
            nestAround { super.visitForExpression(expression) }
        }

        override fun visitKtElement(element: KtElement) {
            val parent = element.parent
            if (element is KtContainerNodeForControlStructureBody && parent is KtIfExpression) {
                when (element.node.elementType) {
                    KtNodeTypes.THEN -> {
                        if (parent.parent.node.elementType == KtNodeTypes.ELSE) {
                            complexity++
                        } else {
                            addComplexity()
                        }
                        nestAround { super.visitKtElement(element) }
                    }

                    KtNodeTypes.ELSE -> {
                        if (element.expression is KtIfExpression) {
                            super.visitKtElement(element)
                        } else {
                            complexity++
                            nestAround { super.visitKtElement(element) }
                        }
                    }

                    else ->
                        super.visitKtElement(element)
                }
            } else {
                super.visitKtElement(element)
            }
        }

        override fun visitBreakExpression(expression: KtBreakExpression) {
            testJumpWithLabel(expression)
            super.visitBreakExpression(expression)
        }

        override fun visitContinueExpression(expression: KtContinueExpression) {
            testJumpWithLabel(expression)
            super.visitContinueExpression(expression)
        }

        override fun visitReturnExpression(expression: KtReturnExpression) {
            testJumpWithLabel(expression)
            super.visitReturnExpression(expression)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            if (function != givenFunction) {
                nestAround { super.visitNamedFunction(function) }
            } else {
                super.visitNamedFunction(function)
            }
        }

        override fun visitCatchSection(catchClause: KtCatchClause) {
            addComplexity()
            nestAround { super.visitCatchSection(catchClause) }
        }

        override fun visitWhileExpression(expression: KtWhileExpression) {
            addComplexity()
            nestAround { super.visitWhileExpression(expression) }
        }

        override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
            addComplexity()
            nestAround { super.visitDoWhileExpression(expression) }
        }

        override fun visitCallExpression(expression: KtCallExpression) {
            if (expression.isRecursion()) {
                complexity++
            }
            super.visitCallExpression(expression)
        }

        override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
            nestAround { super.visitLambdaExpression(lambdaExpression) }
        }

        override fun visitBinaryExpression(expression: KtBinaryExpression) {
            if (topMostBinExpr == null) {
                topMostBinExpr = expression
            }
            super.visitBinaryExpression(expression)
            if (topMostBinExpr == expression) {
                val nestedBinExprs = expression.collectDescendantsOfType<KtBinaryExpression>()
                    .asSequence()
                    .map { BinExprHolder(it, it.operationToken, it.parent is KtParenthesizedExpression) }
                    .filter { it.op in logicalOps }
                    .sortedBy { it.expr.operationReference.textRange.startOffset }
                    .toList()
                calculateBinaryExprComplexity(nestedBinExprs)
                topMostBinExpr = null
            }
        }

        private fun calculateBinaryExprComplexity(usedExpr: List<BinExprHolder>) {
            var lastOp: IElementType? = null
            for (binExpr in usedExpr) {
                if (lastOp == null) {
                    complexity++
                } else if (lastOp != binExpr.op || binExpr.isEnclosed) {
                    complexity++
                }
                lastOp = binExpr.op
            }
        }
    }

    companion object {

        val KEY = Key<Int>("detekt.metrics.cognitive_complexity")

        private val logicalOps = setOf(KtTokens.ANDAND, KtTokens.OROR)

        fun calculate(element: KtElement): Int {
            val visitor = CognitiveComplexity()
            element.accept(visitor)
            return visitor.complexity
        }
    }
}
