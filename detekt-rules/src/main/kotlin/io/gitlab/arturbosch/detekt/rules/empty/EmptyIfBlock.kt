package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * @author Artur Bosch
 */
class EmptyIfBlock(config: Config) : EmptyRule(config) {

	override fun visitIfExpression(expression: KtIfExpression) {
		expression.then?.addFindingIfBlockExprIsEmpty()
		checkThenBodyForLoneSemicolon(expression)
	}

	private fun checkThenBodyForLoneSemicolon(expression: KtIfExpression) {
		val valueOfNextSibling = (expression.nextSibling as? LeafPsiElement)?.elementType as? KtSingleValueToken
		if (valueOfNextSibling?.value?.trim() == ";") {
			report(CodeSmell(issue, Entity.from(expression), message = ""))
		}
	}

}
