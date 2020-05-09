package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtCatchClause

internal const val ALLOWED_EXCEPTION_NAME = "_|(ignore|expected).*"

internal fun KtCatchClause.isAllowedExceptionName(regex: Regex) =
    catchParameter?.identifierName()?.matches(regex) == true
