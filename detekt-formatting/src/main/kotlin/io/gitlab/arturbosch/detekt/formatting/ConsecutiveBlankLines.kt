package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * @author Artur Bosch
 */
class ConsecutiveBlankLines(config: Config) : TokenRule("ConsecutiveBlankLines", config) {

	override fun visitSpaces(context: Context, space: PsiWhiteSpace) {
		val parts = space.text.split("\n")
		if (parts.size > 3) {
			context.report(CodeSmell(ISSUE, Entity.from(space, offset = 2)))
			withAutoCorrect {
				(space as LeafPsiElement).replaceWithText("${parts.first()}\n\n${parts.last()}")
			}
		}
	}

	companion object {
		val ISSUE = Issue("ConsecutiveBlankLines", Issue.Severity.Style)
	}
}