package dev.detekt.psi

import org.jetbrains.kotlin.psi.KtCatchClause

fun KtCatchClause.isAllowedExceptionName(regex: Regex) = catchParameter?.name?.matches(regex) == true
