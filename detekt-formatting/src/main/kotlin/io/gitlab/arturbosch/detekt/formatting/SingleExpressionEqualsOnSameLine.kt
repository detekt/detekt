package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
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
class SingleExpressionEqualsOnSameLine(config: Config = Config.empty) : Rule("SingleExpressionEqualsOnSameLine",
		Severity.Style, config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		function.equalsToken?.let { equals ->
			function.bodyExpression?.let {
				val equalsLine = Location.startLineAndColumn(equals).line
				val (exprStart, exprEnd) = it.startAndEndLine()
				if (equalsLine != exprStart && exprStart == exprEnd) {
					addFindings(CodeSmell(id, Entity.from(equals)))
					withAutoCorrect { alignExpressionToEqualsToken(it, function.equalsToken!!) }
				}
			}
		}
	}

	private fun alignExpressionToEqualsToken(expression: KtExpression, equals: PsiElement) {
		var leaf = expression.prevLeaf()
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
		(equals.node as LeafPsiElement).rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
	}
}