package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

/**
 * @author Artur Bosch
 */

fun LeafPsiElement.trimSpacesAround() {
	trimSpaces { it.prevLeaf() }
	trimSpaces(before = false) { it.nextLeaf() }
}

fun LeafPsiElement.trimSpacesAfter() {
	trimSpaces(before = false) { it.nextLeaf() }
}

fun LeafPsiElement.trimSpacesBefore() {
	trimSpaces { it.prevLeaf() }
}

private fun LeafPsiElement.trimSpaces(before: Boolean = true, function: (PsiElement) -> PsiElement?) {
	var iteration = 0
	var parent = function(this)
	while (parent?.node != null && parent.node.elementType == KtTokens.WHITE_SPACE) {
		val prevParent = function(parent)
		when {
			prevParent?.node?.elementType == KtTokens.WHITE_SPACE -> parent.delete()
			parent.node.text.length > 1 -> (parent as LeafPsiElement).rawReplaceWithText(" ")
			else -> return
		}
		parent = prevParent
		iteration++
	}
	if (iteration == 0) {
		val whiteSpace = PsiWhiteSpaceImpl(" ")
		if (before) rawInsertBeforeMe(whiteSpace) else rawInsertAfterMe(whiteSpace)
	}
}
