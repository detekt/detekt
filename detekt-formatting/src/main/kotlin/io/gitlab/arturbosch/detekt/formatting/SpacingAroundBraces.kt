package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.TokenRule
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
class SpacingAroundBraces(config: Config) : TokenRule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "", Debt.FIVE_MINS)

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
			report(CodeSmell(issue, Entity.from(brace)))
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

		if (!spacingBefore || !spacingAfter) {
			report(CodeSmell(issue, Entity.from(this)))
		}
	}

	private fun LeafPsiElement.isPartOfLambda(prevLeaf: PsiElement?)
			= prevLeaf?.node?.elementType == KtTokens.LPAR &&
			(parent is KtLambdaExpression || parent.parent is KtLambdaExpression)

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
