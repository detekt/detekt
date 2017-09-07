package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import org.jetbrains.kotlin.psi.KtClassOrObject

class ForbiddenClassName(config: Config = Config.empty) : SubRule<KtClassOrObject>(config) {
	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val forbiddenNames = valueOrDefault(FORBIDDEN_NAME, "")
			.split(",")
			.map { it.trim() }
			.filter { it.isNotBlank() }

	override fun apply(element: KtClassOrObject) {
		val name = element.name ?: ""

		val forbiddenEntries = forbiddenNames.filter { name.contains(it, ignoreCase = true) }

		if (forbiddenEntries.isNotEmpty()) {
			var description = "Class name $name is forbidden as it contains:"
			forbiddenEntries.forEach { description += " $it," }
			description.trimEnd { it.equals(",") }

			report(CodeSmell(issue.copy(description = description), Entity.from(element)))
		}
	}

	companion object {
		const val FORBIDDEN_NAME = "forbiddenName"
	}
}
