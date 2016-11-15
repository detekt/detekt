package io.gitlab.arturbosch.detekt.rules.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.rules.head

/**
 * @author Shyiko
 * @author Artur Bosch
 */
class TrailingSpaces(config: Config) : TokenRule("TrailingSpaces", Severity.Style, config) {

	override fun procedure(node: ASTNode) {
		if (node is PsiWhiteSpace && node is LeafPsiElement) {
			val candidates = node.getText().split("\n")
			if (candidates.size > 1) {
				replaceTracingSpaces(node, candidates.head(), replaceWith = "\n".repeat(candidates.size - 1) + candidates.last())
			} else if (PsiTreeUtil.nextLeaf(node) == null /* eof */) {
				replaceTracingSpaces(node, candidates, replaceWith = "\n".repeat(candidates.size - 1))
			}
		}
	}

	private fun replaceTracingSpaces(node: LeafPsiElement, candidates: List<String>, replaceWith: String) {
		if (checkForTrailingSpaces(candidates, node)) {
			withAutoCorrect {
				node.replaceWithText(replaceWith)
			}
		}
	}

	private fun checkForTrailingSpaces(candidates: List<String>, node: PsiElement): Boolean {
		var violationOffset = 0
		var changed = false
		candidates.forEach {
			if (!it.isEmpty()) {
				changed = true
				addFindings(CodeSmell(id, Entity.from(node, offset = violationOffset), "Trailing space(s)"))
			}
			violationOffset += it.length + 1
		}
		return changed
	}

}