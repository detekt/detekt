package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Dept
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * @author Artur Bosch
 */
class WildcardImport(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Wildcard imports should be replaced with imports using fully qualified class names. " +
					"Wildcard imports can lead to naming conflicts. " +
					"A library update can introduce naming clashes with your classes which " +
					"results in compilation errors.",
			Dept.FIVE_MINS)

	override fun visitImportDirective(importDirective: KtImportDirective) {
		val import = importDirective.importPath?.pathStr
		if (import != null && import.contains("*")) {
			report(CodeSmell(issue, Entity.from(importDirective)))
		}
	}
}

