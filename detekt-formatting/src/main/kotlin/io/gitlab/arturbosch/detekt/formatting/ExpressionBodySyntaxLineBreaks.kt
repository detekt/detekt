package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf

/**
 * @author Artur Bosch
 */
class ExpressionBodySyntaxLineBreaks(config: Config = Config.empty) : Rule(
		"ExpressionBodySyntaxLineBreaks", config) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		function.equalsToken?.let { equals ->
			function.bodyExpression?.let { body ->
				checkLineBreaks(context, equals as LeafPsiElement, body)
			}
		}
	}

	private fun checkLineBreaks(context: Context, equals: LeafPsiElement, body: KtExpression) {
		val equalsLine = Location.startLineAndColumn(equals).line
		val (exprStart, exprEnd) = body.startAndEndLine()
		if (equalsLine != exprStart) {
			if (exprStart == exprEnd) {
				context.report(CodeSmell(ISSUE, Entity.from(equals)))
				withAutoCorrect { body.alignToEqualsToken(equals) }
			} else {
				if (equals.trimSpacesBefore(autoCorrect, ignoreLineBreaks = true)) {
					context.report(CodeSmell(ISSUE, Entity.from(equals)))
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

	companion object {
		val ISSUE = Issue("ExpressionBodySyntaxLineBreaks", Issue.Severity.Style)
	}
}