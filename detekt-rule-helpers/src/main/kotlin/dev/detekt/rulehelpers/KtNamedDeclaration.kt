package dev.detekt.rulehelpers

import org.jetbrains.kotlin.psi.KtNamedDeclaration

fun KtNamedDeclaration.isSingleUnderscore(): Boolean = nameIdentifier?.text == "_"
