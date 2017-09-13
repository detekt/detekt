package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject

class UnnecessaryInheritance(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue(javaClass.simpleName, Severity.Style,
			"The extended super type is unnecessary.", Debt.FIVE_MINS)

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		for (superEntry in classOrObject.superTypeListEntries) {
			when (superEntry.text) {
				"Any()" -> report(classOrObject, "Unnecessary inheritance of 'Any'.")
				"Object()" -> report(classOrObject, "Unnecessary inheritance of 'Object'.")
			}
		}
	}

	private fun report(classOrObject: KtClassOrObject, newDescription: String) {
		report(CodeSmell(issue.copy(description = newDescription), Entity.from(classOrObject)))
	}
}
