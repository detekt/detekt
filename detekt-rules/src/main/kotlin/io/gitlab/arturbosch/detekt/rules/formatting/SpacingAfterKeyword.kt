package io.gitlab.arturbosch.detekt.rules.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import org.jetbrains.kotlin.lexer.KtTokens

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class SpacingAfterKeyword(config: Config) : TokenRule("SpacingAfterKeyword", Severity.Style, config) {

	private val tokenSet = TokenSet.create(KtTokens.FOR_KEYWORD, KtTokens.IF_KEYWORD, KtTokens.ELSE_KEYWORD, KtTokens.WHILE_KEYWORD, KtTokens.DO_KEYWORD,
			KtTokens.TRY_KEYWORD, KtTokens.CATCH_KEYWORD, KtTokens.FINALLY_KEYWORD, KtTokens.WHEN_KEYWORD)
	// todo: but not after fun(, get(, set(


	override fun procedure(node: ASTNode) {
		if (tokenSet.contains(node.elementType) && node is LeafPsiElement &&
				PsiTreeUtil.nextLeaf(node) !is PsiWhiteSpace) {
			addFindings(CodeSmell(id, Entity.from(node), "Missing spacing after \"${node.text}\""))
			withAutoCorrect {
				node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
		}
	}

}