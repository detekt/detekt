package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject

class UnnecessarySuperTypeDeclaration(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue(javaClass.simpleName, Severity.Style,
			"The super type declaration is unnecessary.", Debt.FIVE_MINS)

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (hasUnnecessarySuperType(classOrObject)) {
			report(CodeSmell(issue, Entity.from(classOrObject)))
		}
	}

	private fun hasUnnecessarySuperType(classOrObject: KtClassOrObject) =
			classOrObject.superTypeListEntries.any { it.text == "Any()" || it.text == "Object()" }
}
