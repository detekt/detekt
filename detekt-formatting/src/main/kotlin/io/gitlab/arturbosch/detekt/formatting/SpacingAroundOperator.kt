package io.gitlab.arturbosch.detekt.formatting

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
import org.jetbrains.kotlin.lexer.KtTokens.ANDAND
import org.jetbrains.kotlin.lexer.KtTokens.ARROW
import org.jetbrains.kotlin.lexer.KtTokens.DIV
import org.jetbrains.kotlin.lexer.KtTokens.DIVEQ
import org.jetbrains.kotlin.lexer.KtTokens.ELVIS
import org.jetbrains.kotlin.lexer.KtTokens.EQ
import org.jetbrains.kotlin.lexer.KtTokens.EQEQ
import org.jetbrains.kotlin.lexer.KtTokens.EQEQEQ
import org.jetbrains.kotlin.lexer.KtTokens.EXCLEQ
import org.jetbrains.kotlin.lexer.KtTokens.EXCLEQEQEQ
import org.jetbrains.kotlin.lexer.KtTokens.GT
import org.jetbrains.kotlin.lexer.KtTokens.GTEQ
import org.jetbrains.kotlin.lexer.KtTokens.LT
import org.jetbrains.kotlin.lexer.KtTokens.LTEQ
import org.jetbrains.kotlin.lexer.KtTokens.MINUS
import org.jetbrains.kotlin.lexer.KtTokens.MINUSEQ
import org.jetbrains.kotlin.lexer.KtTokens.MUL
import org.jetbrains.kotlin.lexer.KtTokens.MULTEQ
import org.jetbrains.kotlin.lexer.KtTokens.OROR
import org.jetbrains.kotlin.lexer.KtTokens.PERC
import org.jetbrains.kotlin.lexer.KtTokens.PERCEQ
import org.jetbrains.kotlin.lexer.KtTokens.PLUS
import org.jetbrains.kotlin.lexer.KtTokens.PLUSEQ
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeParameterList
import org.jetbrains.kotlin.psi.KtValueArgument

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class SpacingAroundOperator(config: Config) : TokenRule("SpacingAroundOperator", Severity.Style, config) {

	private val tokenSet = TokenSet.create(MUL, PLUS, MINUS, DIV, PERC, LT, GT, LTEQ, GTEQ, EQEQEQ, EXCLEQEQEQ, EQEQ,
			EXCLEQ, ANDAND, OROR, ELVIS, EQ, MULTEQ, DIVEQ, PERCEQ, PLUSEQ, MINUSEQ, ARROW)

	override fun procedure(node: ASTNode) {
		if (tokenSet.contains(node.elementType) && node is LeafPsiElement &&
				!node.isPartOf(KtPrefixExpression::class) && // not unary
				!node.isPartOf(KtTypeParameterList::class) && // fun <T>fn(): T {}
				!node.isPartOf(KtTypeArgumentList::class) && // C<T>
				!node.isPartOf(KtValueArgument::class) && // fn(*array)
				!node.isPartOf(KtImportDirective::class) // import *
		) {
			val spacingBefore = PsiTreeUtil.prevLeaf(node, true) is PsiWhiteSpace
			val spacingAfter = PsiTreeUtil.nextLeaf(node, true) is PsiWhiteSpace
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
					addFindings(CodeSmell(id, Entity.from(node, offset = 1), "Missing spacing after \":\""))
					withAutoCorrect {
						node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
					}
				}
			}
		}
	}

}