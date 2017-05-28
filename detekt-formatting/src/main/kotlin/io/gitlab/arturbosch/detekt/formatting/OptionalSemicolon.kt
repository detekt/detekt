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
class OptionalSemicolon(config: Config = Config.empty) : TokenRule("OptionalSemicolon", Severity.Style, config) {

	override fun visitSemicolon(leaf: LeafPsiElement) {
		if (leaf.isNotPartOfEnum() && leaf.isNotPartOfString()) {
			val nextLeaf = leaf.nextLeaf()
			if (nextLeaf.isSemicolonOrEOF() || nextTokenHasSpaces(nextLeaf)) {
				addFindings(CodeSmell(id, Entity.from(leaf)))
				withAutoCorrect { leaf.delete() }
			}
		}
	}

	override fun visitDoubleSemicolon(leaf: LeafPsiElement) {
		if (leaf.isNotPartOfEnum() && leaf.isNotPartOfString()) {
			addFindings(CodeSmell(id, Entity.from(leaf)))
			withAutoCorrect {
				deleteOneOrTwoSemicolons(leaf)
			}
		}
	}

	private fun deleteOneOrTwoSemicolons(node: LeafPsiElement) {
		val nextLeaf = node.nextLeaf()
		if (nextLeaf.isSemicolonOrEOF() || nextTokenHasSpaces(nextLeaf)) {
			node.delete()
		} else {
			node.replaceWithText(";")
		}
	}

	private fun nextTokenHasSpaces(leaf: PsiElement?) = leaf is PsiWhiteSpace &&
			(leaf.isNewLine() || leaf.nextLeaf().isSemicolonOrEOF())

	private fun PsiElement?.isSemicolonOrEOF() = this == null || isSemicolon() || isDoubleSemicolon()

}

