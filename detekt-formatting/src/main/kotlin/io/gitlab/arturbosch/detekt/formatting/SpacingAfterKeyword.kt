package io.gitlab.arturbosch.detekt.formatting

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
import org.jetbrains.kotlin.lexer.KtTokens
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
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf

/**
 * @author Artur Bosch
 */
class SpacingAfterKeyword(config: Config) : TokenRule("SpacingAfterKeyword", Severity.Style, config) {

	private val keywords = TokenSet.create(FOR_KEYWORD, IF_KEYWORD, ELSE_KEYWORD, WHILE_KEYWORD, DO_KEYWORD,
			TRY_KEYWORD, CATCH_KEYWORD, FINALLY_KEYWORD, WHEN_KEYWORD)

	private val keywordsWithoutSpaces = TokenSet.create(KtTokens.GET_KEYWORD, KtTokens.SET_KEYWORD)

	override fun visitLeaf(leaf: LeafPsiElement) {
		if (keywords.contains(leaf.elementType) && !leaf.nextLeafIsWhiteSpace()) {
			addFindings(CodeSmell(id, Entity.from(leaf, offset = leaf.text.length)))
			withAutoCorrect {
				handleCatchCase(leaf)
				leaf.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
			}
		} else if (keywordsWithoutSpaces.contains(leaf.elementType) && leaf.nextLeafIsWhiteSpace()) {
			addFindings(CodeSmell(id, Entity.from(leaf, offset = leaf.text.length)))
			withAutoCorrect {
				leaf.nextLeaf()?.delete()
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