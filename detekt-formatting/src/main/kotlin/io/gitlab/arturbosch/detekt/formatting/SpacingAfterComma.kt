package io.gitlab.arturbosch.detekt.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.util.PsiTreeUtil
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class SpacingAfterComma(config: Config) : TokenRule("SpacingAfterComma", Severity.Style, config) {

	override fun procedure(node: ASTNode) {
		if (node is LeafPsiElement && (node.textMatches(",") || node.textMatches(";")) && !node.isPartOfString() &&
				PsiTreeUtil.nextLeaf(node) !is PsiWhiteSpace) {
			addFindings(CodeSmell(id, Entity.Companion.from(node, offset = 1), "Missing spacing after \"${node.text}\""))
			withAutoCorrect {
				node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
		}
	}

}