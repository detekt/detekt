package io.github.detekt.metrics

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

class CognitiveComplexity private constructor() : DetektVisitor() {

    private var complexity: Int = 0

    override fun visitNamedFunction(function: KtNamedFunction) {
        val visitor = FunctionComplexity()
        visitor.visitNamedFunction(function)
        complexity += visitor.complexity
    }

    inner class FunctionComplexity : DetektVisitor() {
        internal var complexity: Int = 0
        private var nesting: Int = 0

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

        override fun visitWhenExpression(expression: KtWhenExpression) {
            addComplexity()
            nestAround { super.visitWhenExpression(expression) }
        }

        override fun visitForExpression(expression: KtForExpression) {
            addComplexity()
            nestAround { super.visitForExpression(expression) }
        }

        override fun visitIfExpression(expression: KtIfExpression) {
            addComplexity()
            nestAround { super.visitIfExpression(expression) }
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
    }

    companion object {

        fun calculate(element: KtElement): Int {
            val visitor = CognitiveComplexity()
            element.accept(visitor)
            return visitor.complexity
        }
    }
}
