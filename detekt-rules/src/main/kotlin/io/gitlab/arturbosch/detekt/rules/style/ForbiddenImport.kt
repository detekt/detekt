package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import org.jetbrains.kotlin.psi.KtImportList

/**
 * This rule allows to set a list of forbidden imports. This can be used to discourage the use of unstable, experimental
 * or deprecated APIs. Detekt will then report all imports that are forbidden.
 *
 * <noncompliant>
 * package foo

 * import kotlin.jvm.JvmField
 * import kotlin.SinceKotlin
 * </noncompliant>
 *
 * @configuration imports - imports which should not be used (default: '')
 *
 * @author Niklas Baudy
 * @author Marvin Ramin
 */
class ForbiddenImport(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Mark forbidden imports. A forbidden import could be an import for an unstable / experimental api" +
					"and hence you might want to mark it as forbidden in order to get warned about the usage.", Debt.TEN_MINS)

	private val forbiddenImports = SplitPattern(valueOrDefault(IMPORTS, ""))

	override fun visitImportList(importList: KtImportList) {
		super.visitImportList(importList)

		importList
				.imports
				.filterNot { it.isAllUnder }
				.filter { forbiddenImports.contains(it.importedFqName?.asString() ?: "") }
				.forEach { report(CodeSmell(issue, Entity.from(it), "The import " +
						"${it.importedFqName!!.asString()} has been forbidden in the Detekt config.")) }
	}

	companion object {
		const val IMPORTS = "imports"
	}
}
