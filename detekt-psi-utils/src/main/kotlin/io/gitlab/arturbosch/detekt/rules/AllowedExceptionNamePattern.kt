package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtCatchClause

fun KtCatchClause.isAllowedExceptionName(regex: Regex) =
    catchParameter?.identifierName()?.matches(regex) == true
