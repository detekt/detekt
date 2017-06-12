package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf

/**
 * @author Artur Bosch
 */
class TrailingSpaces(config: Config) : TokenRule("TrailingSpaces", config) {

	override fun visitSpaces(context: Context, space: PsiWhiteSpace) {
		val candidates = space.text.split("\n")
		if (candidates.size > 1) {
			replaceTracingSpaces(context, space, candidates.dropLast(),
					replaceWith = "\n".repeat(candidates.size - 1) + candidates.last())
		} else if (space.nextIsEOF()) {
			replaceTracingSpaces(context, space, candidates,
					replaceWith = "\n".repeat(candidates.size - 1))
		}
	}

	private fun PsiElement.nextIsEOF() = nextLeaf() == null

	private fun replaceTracingSpaces(context: Context,
									 node: PsiWhiteSpace,
									 candidates: List<String>,
									 replaceWith: String) {
		if (candidates.find { !it.isEmpty() } != null) {
			context.report(CodeSmell(ISSUE, Entity.from(node)))
			withAutoCorrect {
				(node as LeafPsiElement).replaceWithText(replaceWith)
			}
		}
	}

	companion object {
		val ISSUE = Issue("TrailingSpaces", Issue.Severity.Style)
	}
}