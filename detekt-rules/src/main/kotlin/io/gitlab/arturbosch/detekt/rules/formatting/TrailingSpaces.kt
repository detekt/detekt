package io.gitlab.arturbosch.detekt.rules.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.rules.head
import org.jetbrains.kotlin.psi.psiUtil.startOffset

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class TrailingSpaces(config: Config) : TokenRule("TrailingSpaces", Severity.Style, config) {

	// TODO#49
	override fun procedure(node: ASTNode) {
		if (node is PsiWhiteSpace) {
			val split = node.getText().split("\n")
			if (split.size > 1) {
				checkForTrailingSpaces(split.head(), node)
//				withAutoCorrect {
//					(node as LeafPsiElement).replaceWithText("\n".repeat(split.size - 1) + split.last())
//				}
			} else
				if (PsiTreeUtil.nextLeaf(node) == null /* eof */) {
					checkForTrailingSpaces(split, node)
//					withAutoCorrect {
//						(node as LeafPsiElement).replaceWithText("\n".repeat(split.size - 1))
//					}
				}
		}
	}

	private fun checkForTrailingSpaces(split: List<String>, node: PsiElement) {
		var violationOffset = node.startOffset
		return split.forEach {
			if (!it.isEmpty()) {
				addFindings(CodeSmell(id, Entity.from(node), "Trailing space(s)"))
			}
			violationOffset += it.length + 1
		}
	}

}