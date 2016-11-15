package io.gitlab.arturbosch.detekt.rules.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import org.jetbrains.kotlin.lexer.KtTokens.CATCH_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.DO_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.ELSE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.FINALLY_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.FOR_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.IF_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.TRY_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.WHEN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.WHILE_KEYWORD
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Shyiko
 * @author Artur Bosch
 */
class SpacingAfterKeyword(config: Config) : TokenRule("SpacingAfterKeyword", Severity.Style, config) {

	private val tokenSet = TokenSet.create(FOR_KEYWORD, IF_KEYWORD, ELSE_KEYWORD, WHILE_KEYWORD, DO_KEYWORD,
			TRY_KEYWORD, CATCH_KEYWORD, FINALLY_KEYWORD, WHEN_KEYWORD)
	// todo: but not after fun(, get(, set(

	override fun procedure(node: ASTNode) {
		if (tokenSet.contains(node.elementType) && node is LeafPsiElement &&
				PsiTreeUtil.nextLeaf(node) !is PsiWhiteSpace) {
			addFindings(CodeSmell(id, Entity.from(node, offset = node.text.length), "Missing spacing after \"${node.text}\""))
			withAutoCorrect {
				handleCatchCase(node)
				node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
		}
	}

	private fun handleCatchCase(node: LeafPsiElement) {
		if (node.elementType == CATCH_KEYWORD) {
			node.rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
			val parent = node.parent
			if (parent is KtCatchClause) {
				handleSpaceAfterCaseParameterList(parent)
			}
		}
	}

	private fun handleSpaceAfterCaseParameterList(parent: KtCatchClause) {
		parent.parameterList?.let {
			val paramList = it.node
			if (PsiTreeUtil.nextLeaf(it) !is PsiWhiteSpace && paramList is TreeElement) {
				paramList.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
		}
	}

}