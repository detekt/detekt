package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class NoDocOverPublicMethod(config: Config = Config.EMPTY) : Rule("NoDocOverPublicMethod", Severity.Maintainability, config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.funKeyword == null && function.isLocal) return
		val modifierList = function.modifierList
		if (function.docComment == null) {
			if (modifierList == null) {
				addFindings(CodeSmell(id, methodHeaderLocation(function)))
			}
			if (modifierList != null) {
				if (modifierList.mustBePublic()) {
					addFindings(CodeSmell(id, methodHeaderLocation(function)))
				}
			}
		}
	}

	private fun methodHeaderLocation(function: KtNamedFunction) = Location.from(function, function.colon)

	private fun KtModifierList.mustBePublic() = (this.hasModifier(KtTokens.PRIVATE_KEYWORD)
			|| this.hasModifier(KtTokens.PROTECTED_KEYWORD)
			|| this.hasModifier(KtTokens.INTERNAL_KEYWORD)).not()
}