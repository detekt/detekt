package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * @author Artur Bosch
 */
class MultipleSpaces(config: Config) : TokenRule("MultipleSpaces", config) {

	override fun visitSpaces(context: Context, space: PsiWhiteSpace) {
		if (!space.textContains('\n') && space.textLength > 1) {
			context.report(CodeSmell(ISSUE, Entity.from(space, offset = 1)))
			withAutoCorrect {
				(space as LeafPsiElement).replaceWithText(" ")
			}
		}
	}

	companion object {
		val ISSUE = Issue("MultipleSpaces", Issue.Severity.Style)
	}
}