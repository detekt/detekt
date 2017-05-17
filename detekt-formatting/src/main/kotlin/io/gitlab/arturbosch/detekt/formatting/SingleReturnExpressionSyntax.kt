package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.FACTORY
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

/**
 * @author Artur Bosch
 */
class SingleReturnExpressionSyntax(config: Config = Config.empty) : Rule("SingleReturnExpressionSyntax",
		Severity.Style, config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.bodyExpression != null) {
			val body = function.bodyExpression!!
			body.singleReturnStatement()?.let { returnStmt ->
				addFindings(CodeSmell(id, Entity.from(body)))
				withAutoCorrect {
					val equals = FACTORY.createEQ().node
					val returnedExpression = returnStmt.returnedExpression!!
					function.node.replaceChild(body.node, returnedExpression.node)
					function.node.removeChild(function.typeReference!!.node)
					function.node.removeChild(function.colon!!.node)
					function.node.addChild(equals, returnedExpression.node)
					(equals as LeafPsiElement).trimSpacesAround()
				}
			}
		}
	}

	private fun KtExpression.singleReturnStatement(): KtReturnExpression? {
		val statements = (this as? KtBlockExpression)?.statements
		return statements?.size?.let {
			if (it == 1 && statements[0] is KtReturnExpression) {
				return statements[0] as KtReturnExpression
			} else null
		}
	}

	private fun LeafPsiElement.trimSpacesAround() {
		trimSpaces { it.prevLeaf() }
		trimSpaces(before = false) { it.nextLeaf() }
	}

	private fun LeafPsiElement.trimSpaces(before: Boolean = true, function: (PsiElement) -> PsiElement?) {
		var iteration = 0
		var parent = function(this)
		while (parent?.node != null && parent.node.elementType == KtTokens.WHITE_SPACE) {
			val prevParent = function(parent)
			when {
				prevParent?.node?.elementType == KtTokens.WHITE_SPACE -> parent.delete()
				parent.node.text.length > 1 -> (parent as LeafPsiElement).rawReplaceWithText(" ")
				else -> return
			}
			parent = prevParent
			iteration++
		}
		if (iteration == 0) {
			val whiteSpace = PsiWhiteSpaceImpl(" ")
			if (before) rawInsertBeforeMe(whiteSpace) else rawInsertAfterMe(whiteSpace)
		}
	}

}
