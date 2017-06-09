package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * @author Artur Bosch
 */
class WildcardImport(config: Config = Config.empty) : Rule("WildcardImport", Severity.Style, config) {

	override fun visitImportDirective(importDirective: KtImportDirective) {
		val import = importDirective.importPath?.pathStr
		if (import != null && import.contains("*")) {
			addFindings(CodeSmell(id, severity, Entity.Companion.from(importDirective)))
		}
	}
}

