package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * @configuration classPattern - naming pattern (default: '[A-Z$][a-zA-Z0-9$]*')
 * @active since v1.0.0
 * @author Marvin Ramin
 */
class ClassNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"A classes name should fit the naming pattern defined in the projects configuration.",
			debt = Debt.FIVE_MINS)
	private val classPattern = Regex(valueOrDefault(CLASS_PATTERN, "^[A-Z$][a-zA-Z0-9$]*$"))

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (!classOrObject.identifierName().matches(classPattern)) {
			report(CodeSmell(
					issue,
					Entity.from(classOrObject),
					message = "Class and Object names should match the pattern: $classPattern"))
		}
	}

	companion object {
		const val CLASS_PATTERN = "classPattern"
	}
}
