package io.gitlab.arturbosch.detekt.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class Indentation(config: Config) : TokenRule("Indentation", Severity.Style, config) {

	override fun procedure(node: ASTNode) {
		if (node is PsiWhiteSpace && !node.isPartOf(PsiComment::class)) {
			val split = node.getText().split("\n")
			if (split.size > 1) {
				var offset = node.startOffset + split.first().length + 1
				split.dropFirst().forEach {
					if (it.length % 4 != 0) {
						addFindings(CodeSmell(id, Entity.from(node, offset = 1), "Unexpected indentation (${it.length})"))
					}
					offset += it.length + 1
				}
			}
		}
	}

}