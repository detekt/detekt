package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import org.jetbrains.kotlin.psi.KtClassOrObject

class ClassNaming(config: Config = Config.empty) : SubRule<KtClassOrObject>(config) {
	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val classPattern = Regex(valueOrDefault(CLASS_PATTERN, "^[A-Z$][a-zA-Z$]*$"))

	override fun apply(element: KtClassOrObject) {
		if (!element.identifierName().matches(classPattern)) {
			report(CodeSmell(
					issue.copy(description = "Class and Object names should match the pattern: $classPattern"),
					Entity.from(element)))
		}
	}

	companion object {
		const val CLASS_PATTERN = "classPattern"
	}
}
