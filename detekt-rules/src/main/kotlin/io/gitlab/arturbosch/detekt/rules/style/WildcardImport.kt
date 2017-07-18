package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
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
			Debt.FIVE_MINS)

	private val excludedImports = Excludes(valueOrDefault(EXCLUDED_IMPORTS, ""))

	override fun visitImportDirective(importDirective: KtImportDirective) {
		val import = importDirective.importPath?.pathStr
		import?.let {
			if (!import.contains("*")) {
				return
			}

			if (excludedImports.contains(import)) {
				return
			}
			report(CodeSmell(issue, Entity.from(importDirective)))
		}
	}

	companion object {
		const val EXCLUDED_IMPORTS = "excludedImports"
	}
}

