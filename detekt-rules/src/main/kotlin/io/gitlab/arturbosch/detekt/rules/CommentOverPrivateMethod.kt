package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.CodeSmellRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class CommentOverPrivateMethod(config: Config = Config.empty) : CodeSmellRule("CommentOverPrivateMethod", config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		val modifierList = function.modifierList
		if (modifierList != null && function.docComment != null) {
			if (modifierList.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
				addFindings(CodeSmell(id, Entity.from(function.docComment!!)))
			}
		}
	}
}