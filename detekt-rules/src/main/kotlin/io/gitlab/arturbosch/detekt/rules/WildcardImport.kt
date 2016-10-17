package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * @author Artur Bosch
 */
class WildcardImport : Rule("WildcardImport", Severity.Style) {

	override fun visitImportDirective(importDirective: KtImportDirective) {
		val import = importDirective.importPath?.pathStr
		if (import != null && import.contains("*")) {
			addFindings(CodeSmell(id, Location.of(importDirective)))
		}
	}
}

