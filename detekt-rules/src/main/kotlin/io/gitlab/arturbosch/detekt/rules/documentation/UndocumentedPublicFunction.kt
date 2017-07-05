package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverriden
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class UndocumentedPublicFunction(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Maintainability,
			"Public functions require documentation.")

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.funKeyword == null && function.isLocal) return

		val modifierList = function.modifierList
		if (function.docComment == null) {
			if (modifierList == null) {
				report(CodeSmell(issue, methodHeaderLocation(function)))
			}
			if (modifierList != null) {
				if (function.isPublicNotOverriden()) {
					report(CodeSmell(issue, methodHeaderLocation(function)))
				}
			}
		}
	}

	private fun methodHeaderLocation(function: KtNamedFunction) = Entity.from(function)

}