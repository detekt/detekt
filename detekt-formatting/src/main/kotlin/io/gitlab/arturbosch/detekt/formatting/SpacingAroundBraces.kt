package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.api.Unstable
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

/**
 * Adapted from KtLint.
 *
 * @author Artur Bosch
 */
@Unstable("This Rule does not add findings as stated in #47")
class SpacingAroundBraces(config: Config, val debug: Boolean = false) : TokenRule(
		"SpacingAroundBraces", Severity.Style, config) {

	override fun visitLeftBrace(brace: LeafPsiElement) {
		val prevLeaf = brace.prevLeaf(skipEmptyElements = true)
		val nextLeaf = brace.nextLeaf(skipEmptyElements = true)
		val spacingBefore = prevLeaf is PsiWhiteSpace || brace.isPartOfLambda(prevLeaf)
		val spacingAfter = nextLeaf is PsiWhiteSpace || nextLeaf?.node?.elementType == KtTokens.RBRACE
		brace.evaluate(spacingBefore, spacingAfter)
	}

	override fun visitRightBrace(brace: LeafPsiElement) {
		val prevLeaf = brace.prevLeaf(skipEmptyElements = true)
		val nextLeaf = brace.nextLeaf(skipEmptyElements = true)
		val spacingBefore = prevLeaf is PsiWhiteSpace || prevLeaf?.node?.elementType == KtTokens.LBRACE
		val spacingAfter = nextLeaf == null || nextLeaf is PsiWhiteSpace || shouldNotToBeSeparatedBySpace(nextLeaf)
		nextLeaf.checkForInvalidSpace(brace)
		brace.evaluate(spacingBefore, spacingAfter)
	}

	private fun PsiElement?.checkForInvalidSpace(brace: LeafPsiElement) {
		if (this is PsiWhiteSpace && !textContains('\n') &&
				shouldNotToBeSeparatedBySpace(nextLeaf(skipEmptyElements = true))) {
			if (debug) addFindings(CodeSmell(id, Entity.from(brace)))
			withAutoCorrect { delete() }
		}
	}

	private fun LeafPsiElement.evaluate(spacingBefore: Boolean, spacingAfter: Boolean) {
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

		if (debug && (!spacingBefore || !spacingAfter)) {
			addFindings(CodeSmell(id, Entity.from(this)))
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

}