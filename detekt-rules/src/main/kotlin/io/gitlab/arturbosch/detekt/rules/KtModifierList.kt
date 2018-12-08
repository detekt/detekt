package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.isPublic

fun KtModifierListOwner.isPublicNotOverridden() =
		isPublic && !isOverridden()

fun KtModifierListOwner.isAbstract() = hasModifier(KtTokens.ABSTRACT_KEYWORD)

fun KtModifierListOwner.isOverridden() = hasModifier(KtTokens.OVERRIDE_KEYWORD)

fun KtModifierListOwner.isOpen() = hasModifier(KtTokens.OPEN_KEYWORD)

fun KtModifierListOwner.isExternal() = hasModifier(KtTokens.EXTERNAL_KEYWORD)

fun KtModifierListOwner.isOperator() = hasModifier(KtTokens.OPERATOR_KEYWORD)

fun KtModifierListOwner.isConstant() = hasModifier(KtTokens.CONST_KEYWORD)

fun KtModifierListOwner.isInternal() = hasModifier(KtTokens.INTERNAL_KEYWORD)
