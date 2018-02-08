package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner

fun KtModifierListOwner.isPublicNotOverridden() =
		isPublic() && !isOverridden()

fun KtModifierListOwner.isOverridden() = hasModifier(KtTokens.OVERRIDE_KEYWORD)

fun KtModifierListOwner.isOpen() = hasModifier(KtTokens.OPEN_KEYWORD)

fun KtModifierListOwner.isPublic(): Boolean {
	return this.hasModifier(KtTokens.PUBLIC_KEYWORD)
			|| !(this.hasModifier(KtTokens.PRIVATE_KEYWORD)
			|| this.hasModifier(KtTokens.PROTECTED_KEYWORD)
			|| this.hasModifier(KtTokens.INTERNAL_KEYWORD))
}

fun KtModifierListOwner.isConstant() = hasModifier(KtTokens.CONST_KEYWORD)

fun KtModifierListOwner.isInternal() = hasModifier(KtTokens.INTERNAL_KEYWORD)
