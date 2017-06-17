package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverriden
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class NoDocOverPublicMethod(config: Config = Config.empty) : Rule("NoDocOverPublicMethod", config) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		if (function.funKeyword == null && function.isLocal) return

		val modifierList = function.modifierList
		if (function.docComment == null) {
			if (modifierList == null) {
				context.report(CodeSmell(ISSUE, methodHeaderLocation(function)))
			}
			if (modifierList != null) {
				if (function.isPublicNotOverriden()) {
					context.report(CodeSmell(ISSUE, methodHeaderLocation(function)))
				}
			}
		}
	}

	private fun methodHeaderLocation(function: KtNamedFunction) = Entity.from(function)

	companion object {
		val ISSUE = Issue("NoDocOverPublicMethod", Issue.Severity.Maintainability)
	}
}