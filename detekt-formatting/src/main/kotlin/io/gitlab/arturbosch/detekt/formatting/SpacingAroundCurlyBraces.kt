package io.gitlab.arturbosch.detekt.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.util.PsiTreeUtil
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.api.Unstable
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * @author Shyiko
 * @author Artur Bosch
 */
@Unstable("This Rule does not add findings as stated in #47")
class SpacingAroundCurlyBraces(config: Config, val debug: Boolean = false) : TokenRule("SpacingAroundCurlyBraces", Severity.Style, config) {

	override fun procedure(node: ASTNode) {
		if (node is LeafPsiElement && !node.isPartOfString()) {
			val prevLeaf = PsiTreeUtil.prevLeaf(node, true)
			val nextLeaf = PsiTreeUtil.nextLeaf(node, true)
			val spacingBefore: Boolean
			val spacingAfter: Boolean
			if (node.textMatches("{")) {
				spacingBefore = prevLeaf is PsiWhiteSpace || (prevLeaf?.node?.elementType == KtTokens.LPAR &&
						(node.parent is KtLambdaExpression || node.parent.parent is KtLambdaExpression))
				spacingAfter = nextLeaf is PsiWhiteSpace || nextLeaf?.node?.elementType == KtTokens.RBRACE
			} else if (node.textMatches("}")) {
				spacingBefore = prevLeaf is PsiWhiteSpace || prevLeaf?.node?.elementType == KtTokens.LBRACE
				spacingAfter = nextLeaf == null || nextLeaf is PsiWhiteSpace || shouldNotToBeSeparatedBySpace(nextLeaf)
				if (nextLeaf is PsiWhiteSpace && !nextLeaf.textContains('\n') &&
						shouldNotToBeSeparatedBySpace(PsiTreeUtil.nextLeaf(nextLeaf, true))) {
					if (debug) addFindings(CodeSmell(id, Entity.from(node), "Unexpected space after \"${node.text}\""))
					withAutoCorrect {
						nextLeaf.delete()
					}
				}
			} else {
				return
			}
			when {
				!spacingBefore && !spacingAfter -> {
					if (debug) addFindings(CodeSmell(id, Entity.from(node), "Missing spacing around \"${node.text}\""))
					withAutoCorrect {
						node.rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
						node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
					}
				}
				!spacingBefore -> {
					if (debug) addFindings(CodeSmell(id, Entity.from(node), "Missing spacing before \"${node.text}\""))
					withAutoCorrect {
						node.rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
					}
				}
				!spacingAfter -> {
					if (debug) addFindings(CodeSmell(id, Entity.from(node, offset = 1), "Missing spacing after \"${node.text}\""))
					withAutoCorrect {
						node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
					}
				}
			}
		}
	}

	fun shouldNotToBeSeparatedBySpace(leaf: PsiElement?): Boolean {
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