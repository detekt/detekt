package dev.detekt.psi

import org.jetbrains.kotlin.psi.KtNamedDeclaration

fun KtNamedDeclaration.isSingleUnderscore(): Boolean = nameIdentifier?.text == "_"
