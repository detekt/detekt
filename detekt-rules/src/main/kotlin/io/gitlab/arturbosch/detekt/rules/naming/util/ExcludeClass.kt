package io.gitlab.arturbosch.detekt.rules.naming.util

import io.gitlab.arturbosch.detekt.rules.naming.identifierName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

internal fun KtDeclaration.isContainingExcludedClass(pattern: Regex) =
		containingClassOrObject?.identifierName()?.matches(pattern) == true
