package io.gitlab.arturbosch.detekt.formatting

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

fun LeafPsiElement.trimSpacesAround(autoCorrect: Boolean = true, ignoreLineBreaks: Boolean = false): Boolean {
	val before = trimSpaces(autoCorrect, ignoreLineBreaks) { it.prevLeaf() }
	val after = trimSpaces(autoCorrect, ignoreLineBreaks, before = false) { it.nextLeaf() }
	return before || after
}

fun LeafPsiElement.trimSpacesAfter(autoCorrect: Boolean = true, ignoreLineBreaks: Boolean = false)
		= trimSpaces(autoCorrect, ignoreLineBreaks, before = false) { it.nextLeaf() }

fun LeafPsiElement.trimSpacesBefore(autoCorrect: Boolean = true, ignoreLineBreaks: Boolean = false)
		= trimSpaces(autoCorrect, ignoreLineBreaks) { it.prevLeaf() }

private fun LeafPsiElement.trimSpaces(
		autoCorrect: Boolean = true,
		ignoreLineBreaks: Boolean = false,
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
			if (!ignoreLineBreaks) parent.node.text.length > 1 && "\n" !in parent.text else parent.node.text.length > 1 -> {
				if (autoCorrect) (parent as LeafPsiElement).rawReplaceWithText(" ")
				modified = true
			}
			else -> return modified
		}
		parent = prevParent
		iteration++
	}

	if (iteration == 0 && this !is PsiWhiteSpace) {
		if (autoCorrect) {
			val whiteSpace = PsiWhiteSpaceImpl(" ")
			if (before) rawInsertBeforeMe(whiteSpace)
			else rawInsertAfterMe(whiteSpace)
		}
		modified = true
	}

	return modified
}
