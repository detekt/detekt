package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

class EmptyDefaultConstructor(config: Config) : EmptyRule(config = config) {

	override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
		if (hasPublicVisibility(constructor.visibilityModifierType())
				&& constructor.annotationEntries.isEmpty()
				&& constructor.valueParameters.isEmpty()) {
			report(CodeSmell(issue, Entity.from(constructor)))
		}
	}

	private fun hasPublicVisibility(visibility: KtModifierKeywordToken?): Boolean {
		return visibility == null || visibility == KtTokens.PUBLIC_KEYWORD
	}
}
