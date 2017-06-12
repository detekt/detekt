package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl

/**
 * @author Artur Bosch
 */
class SpacingAfterComma(config: Config) : TokenRule("SpacingAfterComma", config) {

	override fun visitComma(context: Context, leaf: LeafPsiElement) {
		checkSpace(context, leaf)
	}

	override fun visitSemicolon(context: Context, leaf: LeafPsiElement) {
		checkSpace(context, leaf)
	}

	private fun checkSpace(context: Context, leaf: LeafPsiElement) {
		if (leaf.isSpaceMissing()) {
			context.report(CodeSmell(ISSUE, Entity.from(leaf, offset = 1)))
			withAutoCorrect {
				leaf.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
		}
	}

	private fun LeafPsiElement.isSpaceMissing() = !isPartOfString() && !nextLeafIsWhiteSpace()

	companion object {
		val ISSUE = Issue("SpacingAfterComma", Issue.Severity.Style)
	}
}