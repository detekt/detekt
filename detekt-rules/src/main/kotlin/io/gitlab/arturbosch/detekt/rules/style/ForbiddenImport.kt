package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Excludes
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtImportList

class ForbiddenImport(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Mark forbidden imports.", Debt.TEN_MINS)

	private val forbiddenImports = Excludes(valueOrDefault(IMPORTS, ""))

	override fun visitImportList(importList: KtImportList) {
		super.visitImportList(importList)

		importList
				.imports
				.filterNot { it.isAllUnder }
				.filter { forbiddenImports.contains(it.importedFqName?.asString() ?: "") }
				.forEach { report(CodeSmell(issue, Entity.from(it))) }
	}

	companion object {
		const val IMPORTS = "imports"
	}
}
