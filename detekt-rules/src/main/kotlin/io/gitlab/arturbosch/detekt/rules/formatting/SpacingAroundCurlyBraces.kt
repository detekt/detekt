package io.gitlab.arturbosch.detekt.rules.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.util.PsiTreeUtil
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.rules.isPartOfString
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class SpacingAroundCurlyBraces(config: Config) : TokenRule("SpacingAroundCurlyBraces", Severity.Style, config) {

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
			} else
				if (node.textMatches("}")) {
					spacingBefore = prevLeaf is PsiWhiteSpace || prevLeaf?.node?.elementType == KtTokens.LBRACE
					val nextElementType = nextLeaf?.node?.elementType
					spacingAfter = nextLeaf == null || nextLeaf is PsiWhiteSpace ||
							nextElementType == KtTokens.DOT ||
							nextElementType == KtTokens.COMMA ||
							nextElementType == KtTokens.RPAR ||
							nextElementType == KtTokens.SEMICOLON ||
							nextElementType == KtTokens.SAFE_ACCESS
				} else {
					return
				}
			when {
				!spacingBefore && !spacingAfter -> {
					addFindings(CodeSmell(id, Entity.from(node), "Missing spacing around \":\""))
					withAutoCorrect {
						node.rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
						node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
					}
				}
				!spacingBefore -> {
					addFindings(CodeSmell(id, Entity.from(node), "Missing spacing before \":\""))
					withAutoCorrect {
						node.rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
					}
				}
				!spacingAfter -> {
					addFindings(CodeSmell(id, Entity.from(node), "Missing spacing after \":\""))
					withAutoCorrect {
						node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
					}
				}
			}
		}
	}

}