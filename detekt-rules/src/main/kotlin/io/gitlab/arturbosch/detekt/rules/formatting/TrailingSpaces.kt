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
			val split = node.getText().split("\n")
			if (split.size > 1) {
				if (checkForTrailingSpaces(split.head(), node)) {
					withAutoCorrect {
						node.replaceWithText("\n".repeat(split.size - 1) + split.last())
					}
				}
			} else
				if (PsiTreeUtil.nextLeaf(node) == null /* eof */) {
					if (checkForTrailingSpaces(split, node)) {
						withAutoCorrect {
							node.replaceWithText("\n".repeat(split.size - 1))
						}
					}
				}
		}
	}

	private fun checkForTrailingSpaces(split: List<String>, node: PsiElement): Boolean {
		var violationOffset = 0
		var changed = false
		split.forEach {
			if (!it.isEmpty()) {
				changed = true
				addFindings(CodeSmell(id, Entity.from(node, offset = violationOffset), "Trailing space(s)"))
			}
			violationOffset += it.length + 1
		}
		return changed
	}

}