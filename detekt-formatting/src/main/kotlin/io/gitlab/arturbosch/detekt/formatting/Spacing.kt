package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

/**
 * @author Artur Bosch
 */

fun LeafPsiElement.nextLeafIsWhiteSpace() = PsiTreeUtil.nextLeaf(this) is PsiWhiteSpace

fun LeafPsiElement.trimSpacesAround(autoCorrect: Boolean = true): Boolean {
	val before = trimSpaces(autoCorrect) { it.prevLeaf() }
	val after = trimSpaces(autoCorrect, before = false) { it.nextLeaf() }
	return before || after
}

fun LeafPsiElement.trimSpacesAfter(autoCorrect: Boolean = true) =
		trimSpaces(autoCorrect, before = false) { it.nextLeaf() }

fun LeafPsiElement.trimSpacesBefore(autoCorrect: Boolean = true) = trimSpaces(autoCorrect) { it.prevLeaf() }

private fun LeafPsiElement.trimSpaces(
		autoCorrect: Boolean = true,
		before: Boolean = true,
		function: (PsiElement) -> PsiElement?): Boolean {

	var iteration = 0
	var modified = false
	var parent = function(this)
	while (parent?.node != null && parent.node.elementType == KtTokens.WHITE_SPACE) {
		val prevParent = function(parent)
		when {
			prevParent?.node?.elementType == KtTokens.WHITE_SPACE -> {
				if (autoCorrect) parent.delete()
				modified = true
			}
			parent.node.text.length > 1 -> {
				if (autoCorrect) (parent as LeafPsiElement).rawReplaceWithText(" ")
				modified = true
			}
			else -> return modified
		}
		parent = prevParent
		iteration++
	}
	if (iteration == 0) {
		if (autoCorrect) {
			val whiteSpace = PsiWhiteSpaceImpl(" ")
			if (before) rawInsertBeforeMe(whiteSpace)
			else rawInsertAfterMe(whiteSpace)
		}
		modified = true
	}

	return modified
}