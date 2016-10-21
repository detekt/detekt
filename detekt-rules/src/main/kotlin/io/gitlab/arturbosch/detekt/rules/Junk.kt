package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtModifierListOwner

/**
 * @author Artur Bosch
 */

fun KtExpression?.asBlockExpression(): KtBlockExpression? = if (this is KtBlockExpression) this else null

fun KtModifierListOwner.isPublicNotOverriden() = this.hasModifier(KtTokens.PUBLIC_KEYWORD)
		|| (this.hasModifier(KtTokens.PRIVATE_KEYWORD) || this.hasModifier(KtTokens.PROTECTED_KEYWORD)
		|| this.hasModifier(KtTokens.INTERNAL_KEYWORD) || this.hasModifier(KtTokens.OVERRIDE_KEYWORD)).not()