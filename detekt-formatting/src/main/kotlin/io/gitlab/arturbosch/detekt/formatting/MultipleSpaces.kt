package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * @author Artur Bosch
 */
class MultipleSpaces(config: Config) : TokenRule("MultipleSpaces", Severity.Style, config) {

	override fun visitSpaces(space: PsiWhiteSpace) {
		if (!space.textContains('\n') && space.textLength > 1) {
			addFindings(CodeSmell(id, Entity.from(space, offset = 1)))
			withAutoCorrect {
				(space as LeafPsiElement).replaceWithText(" ")
			}
		}
	}

}