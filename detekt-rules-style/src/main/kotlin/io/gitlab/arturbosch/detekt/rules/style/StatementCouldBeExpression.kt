package io.gitlab.arturbosch.detekt.rules.style

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReturnExpression

internal fun <T : KtElement> canLiftOut(entries: List<T>): Boolean {
    return canLiftOutReturn(entries) || canLiftOutAssignment(entries)
}

private fun <T : KtElement> canLiftOutReturn(entries: List<T>): Boolean {
    return entries.all { it.children[it.children.lastIndex] is KtReturnExpression }
}

private fun <T : KtElement> canLiftOutAssignment(entries: List<T>): Boolean {
    val lastExpressionsWhichAreBinary = entries.map { it.children[it.children.lastIndex] }
        .filterIsInstance<KtBinaryExpression>()
    val everyLastExpressionIsBinary = lastExpressionsWhichAreBinary.size == entries.size
    return everyLastExpressionIsBinary && areSameAssignmentToSameVariable(lastExpressionsWhichAreBinary)
}

private fun areSameAssignmentToSameVariable(expressions: List<KtBinaryExpression>): Boolean {
    return haveSameLhs(expressions) && haveSameOperator(expressions) && haveAssignmentOperator(expressions)
}

private fun haveSameLhs(expressions: List<KtBinaryExpression>): Boolean {
    return expressions.isEmpty() || expressions.all { expressions[0].left?.text == it.left?.text }
}

private fun haveSameOperator(expressions: List<KtBinaryExpression>): Boolean {
    return expressions.isEmpty() || expressions.all { expressions[0].operationToken == it.operationToken }
}

private fun haveAssignmentOperator(expressions: List<KtBinaryExpression>): Boolean {
    return expressions.all { it.operationToken in KtTokens.ALL_ASSIGNMENTS }
}
