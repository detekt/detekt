@file:Suppress("TooManyFunctions")

package dev.detekt.psi

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.isPublic

fun KtModifierListOwner.isPublicNotOverridden() =
    isPublicNotOverridden(false)

fun KtModifierListOwner.isPublicNotOverridden(considerProtectedAsPublic: Boolean) =
    if (considerProtectedAsPublic) {
        isPublic || isProtected()
    } else {
        isPublic
    } &&
        !isOverride()

fun KtModifierListOwner.isAbstract() = hasModifier(KtTokens.ABSTRACT_KEYWORD)

fun KtModifierListOwner.isOverride() = hasModifier(KtTokens.OVERRIDE_KEYWORD)

fun KtModifierListOwner.isOpen() = hasModifier(KtTokens.OPEN_KEYWORD)

fun KtModifierListOwner.isExternal() = hasModifier(KtTokens.EXTERNAL_KEYWORD)

fun KtModifierListOwner.isOperator() = hasModifier(KtTokens.OPERATOR_KEYWORD)

fun KtModifierListOwner.isConstant() = hasModifier(KtTokens.CONST_KEYWORD)

fun KtModifierListOwner.isInternal() = hasModifier(KtTokens.INTERNAL_KEYWORD)

fun KtModifierListOwner.isLateinit() = hasModifier(KtTokens.LATEINIT_KEYWORD)

fun KtModifierListOwner.isInline() = hasModifier(KtTokens.INLINE_KEYWORD)

fun KtModifierListOwner.isExpect() = hasModifier(KtTokens.EXPECT_KEYWORD)

fun KtModifierListOwner.isActual() = hasModifier(KtTokens.ACTUAL_KEYWORD)

fun KtModifierListOwner.isProtected() = hasModifier(KtTokens.PROTECTED_KEYWORD)
