package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf

/**
 * @author Artur Bosch
 */
class TrailingSpaces(config: Config) : TokenRule("TrailingSpaces", Severity.Style, config) {

	override fun visitSpaces(space: PsiWhiteSpace) {
		val candidates = space.text.split("\n")
		if (candidates.size > 1) {
			replaceTracingSpaces(space, candidates.dropLast(),
					replaceWith = "\n".repeat(candidates.size - 1) + candidates.last())
		} else if (space.nextIsEOF()) {
			replaceTracingSpaces(space, candidates,
					replaceWith = "\n".repeat(candidates.size - 1))
		}
	}

	private fun PsiElement.nextIsEOF() = nextLeaf() == null

	private fun replaceTracingSpaces(node: PsiWhiteSpace, candidates: List<String>, replaceWith: String) {
		if (candidates.find { !it.isEmpty() } != null) {
			addFindings(CodeSmell(id, Entity.from(node)))
			withAutoCorrect {
				(node as LeafPsiElement).replaceWithText(replaceWith)
			}
		}
	}

}