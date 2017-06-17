package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class CommentOverPrivateMethod(config: Config = Config.empty) : Rule("CommentOverPrivateMethod", config) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		val modifierList = function.modifierList
		if (modifierList != null && function.docComment != null) {
			if (modifierList.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
				context.report(CodeSmell(ISSUE, Entity.Companion.from(function.docComment!!)))
			}
		}
	}

	companion object {
		val ISSUE = Issue("CommentOverPrivateMethod", Issue.Severity.CodeSmell)
	}
}