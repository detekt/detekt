package io.gitlab.arturbosch.detekt.rules.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class MultipleSpaces(config: Config) : TokenRule("MultipleSpaces", Severity.Style, config) {

	override fun procedure(node: ASTNode) {
		if (node is PsiWhiteSpace && !node.textContains('\n') && node.getTextLength() > 1) {
			addFindings(CodeSmell(id, Entity.from(node, offset = 1), "Unnecessary space(s)"))
			withAutoCorrect {
				(node as LeafPsiElement).replaceWithText(" ")
			}
		}
	}

}