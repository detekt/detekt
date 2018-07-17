package io.gitlab.arturbosch.detekt.rules.documentation

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration

internal fun KtDeclaration.hasCommentInPrivateMember(): Boolean {
	val modifiers = this.modifierList
	return modifiers != null && docComment != null && modifiers.hasModifier(KtTokens.PRIVATE_KEYWORD)
}
