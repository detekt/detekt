package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class CommentOverPrivateFunction(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("CommentOverPrivateFunction",
			Severity.Maintainability,
			"Comments for private functions should be avoided. " +
					"Prefer giving the function an expressive name. " +
					"Split it up in smaller, self-explaining functions if necessary.")

	override fun visitNamedFunction(function: KtNamedFunction) {
		val modifierList = function.modifierList
		if (modifierList != null && function.docComment != null) {
			if (modifierList.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
				report(CodeSmell(issue, Entity.from(function.docComment!!)))
			}
		}
	}
}
