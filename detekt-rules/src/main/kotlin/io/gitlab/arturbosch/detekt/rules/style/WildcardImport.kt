package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * @author Artur Bosch
 */
class WildcardImport(config: Config = Config.empty) : Rule("WildcardImport", config) {

	override fun visitImportDirective(context: Context, importDirective: KtImportDirective) {
		val import = importDirective.importPath?.pathStr
		if (import != null && import.contains("*")) {
			context.report(CodeSmell(ISSUE, Entity.Companion.from(importDirective)))
		}
	}

	companion object {
		val ISSUE = Issue("WildcardImport", Issue.Severity.Style)
	}
}

