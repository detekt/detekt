package io.gitlab.arturbosch.detekt.rules.naming.util

import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

internal fun KtDeclaration.isContainingExcludedClassOrObject(pattern: Regex) =
        containingClassOrObject?.identifierName()?.matches(pattern) == true

internal fun KtDeclaration.isContainingExcludedClass(pattern: Regex) =
        containingClass()?.identifierName()?.matches(pattern) == true
