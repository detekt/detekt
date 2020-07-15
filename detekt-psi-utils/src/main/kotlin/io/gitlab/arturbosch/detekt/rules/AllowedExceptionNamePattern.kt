package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtCatchClause

const val ALLOWED_EXCEPTION_NAME = "_|(ignore|expected).*"

fun KtCatchClause.isAllowedExceptionName(regex: Regex) =
    catchParameter?.identifierName()?.matches(regex) == true
