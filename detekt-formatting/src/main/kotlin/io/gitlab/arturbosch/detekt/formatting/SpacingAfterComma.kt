package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.api.isPartOfString
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl

/**
 * @author Artur Bosch
 */
class SpacingAfterComma(config: Config) : TokenRule("SpacingAfterComma", Severity.Style, config) {

	override fun visitComma(leaf: LeafPsiElement) {
		checkSpace(leaf)
	}

	override fun visitSemicolon(leaf: LeafPsiElement) {
		checkSpace(leaf)
	}

	private fun checkSpace(leaf: LeafPsiElement) {
		if (leaf.isSpaceMissing()) {
			report(CodeSmell(id, severity, Entity.from(leaf, offset = 1)))
			withAutoCorrect {
				leaf.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
		}
	}

	private fun LeafPsiElement.isSpaceMissing() = !isPartOfString() && !nextLeafIsWhiteSpace()

}