package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtProperty

/**
 * @author Artur Bosch
 */
class CommentOverPrivateProperty(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("CommentOverPrivateProperty",
			Severity.Maintainability,
			"Private properties should be named such that they explain themselves.")

	override fun visitProperty(property: KtProperty) {
		val modifierList = property.modifierList
		if (modifierList != null && property.docComment != null) {
			if (modifierList.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
				report(CodeSmell(issue, Entity.from(property.docComment!!)))
			}
		}
	}

}
