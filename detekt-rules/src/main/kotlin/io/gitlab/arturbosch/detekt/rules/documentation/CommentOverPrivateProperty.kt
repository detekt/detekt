package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtProperty

/**
 * @author Artur Bosch
 */
class CommentOverPrivateProperty(config: Config = Config.empty) : Rule("CommentOverPrivateProperty", config) {

	override fun visitProperty(context: Context, property: KtProperty) {
		val modifierList = property.modifierList
		if (modifierList != null && property.docComment != null) {
			if (modifierList.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
				context.report(CodeSmell(ISSUE, Entity.from(property.docComment!!)))
			}
		}
	}

	companion object {
		val ISSUE = Issue("CommentOverPrivateProperty", Issue.Severity.CodeSmell)
	}
}