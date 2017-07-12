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
class CommentOverPrivateMethod(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("CommentOverPrivateMethod",
			Severity.Maintainability,
			"Comments for private methods should be avoided. " +
					"Prefer giving the method an expressive name. " +
					"Split it up in smaller, self-explaining methods if necessary.")

	override fun visitNamedFunction(function: KtNamedFunction) {
		val modifierList = function.modifierList
		if (modifierList != null && function.docComment != null) {
			if (modifierList.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
				report(CodeSmell(issue, Entity.from(function.docComment!!)))
			}
		}
	}
}
