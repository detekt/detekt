package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Excludes
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject

class ForbiddenClassName(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val forbiddenNames = Excludes(valueOrDefault(FORBIDDEN_NAME, ""))

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		val name = classOrObject.name ?: ""
		val forbiddenEntries = forbiddenNames.matches(name)

		if (forbiddenEntries.isNotEmpty()) {
			var description = "Class name $name is forbidden as it contains:"
			forbiddenEntries.forEach { description += " $it," }
			description.trimEnd { it.equals(",") }

			report(CodeSmell(issue.copy(description = description), Entity.from(classOrObject)))
		}
	}

	companion object {
		const val FORBIDDEN_NAME = "forbiddenName"
	}
}
