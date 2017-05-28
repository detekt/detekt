package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

/**
 * @author Artur Bosch
 */
class ExpressionBodySyntaxLineBreaks(config: Config = Config.empty) : Rule(
		"ExpressionBodySyntaxLineBreaks", Severity.Style, config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		function.equalsToken?.let { equals ->
			function.bodyExpression?.let { body ->
				checkLineBreaks(equals as LeafPsiElement, body)
			}
		}
	}

	private fun checkLineBreaks(equals: LeafPsiElement, body: KtExpression) {
		val equalsLine = Location.startLineAndColumn(equals).line
		val (exprStart, exprEnd) = body.startAndEndLine()
		if (equalsLine != exprStart) {
			if (exprStart == exprEnd) {
				addFindings(CodeSmell(id, Entity.from(equals)))
				withAutoCorrect { body.alignToEqualsToken(equals) }
			} else {
				if (equals.trimSpacesBefore(autoCorrect)) {
					addFindings(CodeSmell(id, Entity.from(equals)))
				}
			}
		}
	}

	private fun KtExpression.alignToEqualsToken(equals: LeafPsiElement) {
		var leaf = prevLeaf()
		while (leaf != null && leaf.node.elementType != KtTokens.EQ) {
			val parent = leaf.prevLeaf()
			val elementType = leaf.node?.elementType
			if (elementType == KtTokens.WHITE_SPACE) {
				if (parent?.node?.elementType in KtTokens.COMMENTS) {
					(leaf as LeafPsiElement).rawReplaceWithText("\n")
				} else {
					leaf.delete()
				}
			}
			leaf = parent
		}
		equals.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
	}
}