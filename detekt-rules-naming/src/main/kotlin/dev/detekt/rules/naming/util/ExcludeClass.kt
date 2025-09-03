package dev.detekt.rules.naming.util

import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

internal fun KtDeclaration.isContainingExcludedClassOrObject(pattern: Regex) =
    containingClassOrObject?.name?.matches(pattern) == true

internal fun KtDeclaration.isContainingExcludedClass(pattern: Regex) =
    containingClass()?.name?.matches(pattern) == true
