package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule will report any public function which does not have the required documentation.
 * If the codebase should have documentation on all public functions enable this rule to enforce this.
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class UndocumentedPublicFunction(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Maintainability,
			"Public functions require documentation.", Debt.TWENTY_MINS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.funKeyword == null && function.isLocal) return

		val modifierList = function.modifierList
		if (function.docComment == null) {
			if (modifierList == null) {
				report(CodeSmell(issue, methodHeaderLocation(function), "The function ${function.nameAsSafeName}" +
						" is missing documentation."))
			}
			if (modifierList != null) {
				if (function.isPublicNotOverridden()) {
					report(CodeSmell(issue, methodHeaderLocation(function), "The function ${function.nameAsSafeName}" +
							" is missing documentation."))
				}
			}
		}
	}

	private fun methodHeaderLocation(function: KtNamedFunction) = Entity.from(function)

}
