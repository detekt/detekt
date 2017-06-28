package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Dept
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile

class MaxLineLength(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "Line detected that is longer than the defined maximum line length in the code style.", Dept.FIVE_MINS)

	private val maxLineLength: Int
			= valueOrDefault(MAX_LINE_LENGTH, DEFAULT_IDEA_LINE_LENGTH)
	private val excludePackageStatements: Boolean
			= valueOrDefault(EXCLUDE_PACKAGE_STATEMENTS, DEFAULT_VALUE_PACKAGE_EXCLUDE)
	private val excludeImportStatements: Boolean
			= valueOrDefault(EXCLUDE_IMPORT_STATEMENTS, DEFAULT_VALUE_IMPORTS_EXCLUDE)

	override fun visitKtFile(file: KtFile) {
		var offset = 0
		file.text.splitToSequence("\n")
				.filter { filterPackageStatements(it) }
				.filter { filterImportStatements(it) }
				.map { it.length }
				.forEach {
					offset += it
					if (it > maxLineLength) {
						report(CodeSmell(issue, Entity.from(file, offset)))
					}
				}
	}

	private fun filterPackageStatements(line: String): Boolean {
		if (excludePackageStatements) {
			return !line.startsWith("package ")
		}
		return true
	}

	private fun filterImportStatements(line: String): Boolean {
		if (excludeImportStatements) {
			return !line.startsWith("import ")
		}
		return true
	}

	companion object {
		const val MAX_LINE_LENGTH = "maxLineLength"
		const val DEFAULT_IDEA_LINE_LENGTH = 120

		const val EXCLUDE_PACKAGE_STATEMENTS = "excludePackageStatements"
		const val DEFAULT_VALUE_PACKAGE_EXCLUDE = false

		const val EXCLUDE_IMPORT_STATEMENTS = "excludeImportStatements"
		const val DEFAULT_VALUE_IMPORTS_EXCLUDE = false
	}
}

