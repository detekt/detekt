package dev.detekt.generator.collection

import org.jetbrains.kotlin.lexer.KtTokens.FALSE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.TRUE_KEYWORD

fun createDefaultValueIfLiteral(maybeLiteral: String): DefaultValue? = maybeLiteral.toDefaultValueIfLiteral()

private fun String.toDefaultValueIfLiteral(): DefaultValue? =
    when {
        isStringLiteral() -> DefaultValue.of(withoutQuotes())
        isBooleanLiteral() -> DefaultValue.of(toBoolean())
        isIntegerLiteral() -> DefaultValue.of(withoutUnderscores().toInt())
        else -> null
    }

private fun String.withoutUnderscores() = replace("_", "")
private fun String.isStringLiteral() = length > 1 && startsWith(QUOTES) && endsWith(QUOTES)
private fun String.isBooleanLiteral() = this == TRUE_KEYWORD.value || this == FALSE_KEYWORD.value
private fun String.isIntegerLiteral() = withoutUnderscores().toIntOrNull() != null

private const val QUOTES = "\""
