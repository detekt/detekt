package dev.detekt.psi

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression

fun KtBinaryExpression.isNonNullCheck(): Boolean =
    operationToken == KtTokens.EXCLEQ && (left?.text == NULL_TEXT || right?.text == NULL_TEXT)

fun KtBinaryExpression.isNullCheck(): Boolean =
    operationToken == KtTokens.EQEQ && (left?.text == NULL_TEXT || right?.text == NULL_TEXT)

private const val NULL_TEXT = "null"
