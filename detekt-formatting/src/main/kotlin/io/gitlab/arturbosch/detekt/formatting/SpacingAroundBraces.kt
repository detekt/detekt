package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

/**
 * Adapted from KtLint.
 *
 * @author Artur Bosch
 */
class SpacingAroundBraces(config: Config) : TokenRule(
		"SpacingAroundBraces", config) {

	override fun visitLeftBrace(context: Context, brace: LeafPsiElement) {
		val prevLeaf = brace.prevLeaf(skipEmptyElements = true)
		val nextLeaf = brace.nextLeaf(skipEmptyElements = true)
		val spacingBefore = prevLeaf is PsiWhiteSpace || brace.isPartOfLambda(prevLeaf)
		val spacingAfter = nextLeaf is PsiWhiteSpace || nextLeaf?.node?.elementType == KtTokens.RBRACE
		brace.evaluate(context, spacingBefore, spacingAfter)
	}

	override fun visitRightBrace(context: Context, brace: LeafPsiElement) {
		val prevLeaf = brace.prevLeaf(skipEmptyElements = true)
		val nextLeaf = brace.nextLeaf(skipEmptyElements = true)
		val spacingBefore = prevLeaf is PsiWhiteSpace || prevLeaf?.node?.elementType == KtTokens.LBRACE
		val spacingAfter = nextLeaf == null || nextLeaf is PsiWhiteSpace || shouldNotToBeSeparatedBySpace(nextLeaf)
		nextLeaf.checkForInvalidSpace(context, brace)
		brace.evaluate(context, spacingBefore, spacingAfter)
	}

	private fun PsiElement?.checkForInvalidSpace(context: Context, brace: LeafPsiElement) {
		if (this is PsiWhiteSpace && !textContains('\n') &&
				shouldNotToBeSeparatedBySpace(nextLeaf(skipEmptyElements = true))) {
			context.report(CodeSmell(ISSUE, Entity.from(brace)))
			withAutoCorrect { delete() }
		}
	}

	private fun LeafPsiElement.evaluate(context: Context, spacingBefore: Boolean, spacingAfter: Boolean) {
		when {
			!spacingBefore && !spacingAfter -> withAutoCorrect {
				rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
				rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
			!spacingBefore -> withAutoCorrect {
				rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
			}
			!spacingAfter -> withAutoCorrect {
				rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
		}

		if (!spacingBefore || !spacingAfter) {
			context.report(CodeSmell(ISSUE, Entity.from(this)))
		}
	}

	private fun LeafPsiElement.isPartOfLambda(prevLeaf: PsiElement?)
			= (prevLeaf?.node?.elementType == KtTokens.LPAR &&
			(parent is KtLambdaExpression || parent.parent is KtLambdaExpression))

	private fun shouldNotToBeSeparatedBySpace(leaf: PsiElement?): Boolean {
		val nextElementType = leaf?.node?.elementType
		return nextElementType != null &&
				nextElementType == KtTokens.DOT ||
				nextElementType == KtTokens.COMMA ||
				nextElementType == KtTokens.RPAR ||
				nextElementType == KtTokens.SEMICOLON ||
				nextElementType == KtTokens.SAFE_ACCESS ||
				nextElementType == KtTokens.EXCLEXCL
	}

	companion object {
		val ISSUE = Issue("SpacingAroundBraces", Issue.Severity.Style)
	}
}