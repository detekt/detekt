package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression

fun KtBinaryExpression.isNonNullCheck(): Boolean {
    return operationToken == KtTokens.EXCLEQ && (left?.text == "null" || right?.text == "null")
}

fun KtBinaryExpression.isNullCheck(): Boolean {
    return operationToken == KtTokens.EQEQ && (left?.text == "null" || right?.text == "null")
}
