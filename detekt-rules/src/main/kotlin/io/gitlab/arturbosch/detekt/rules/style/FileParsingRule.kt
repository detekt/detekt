package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import io.gitlab.arturbosch.detekt.rules.reportFindings
import org.jetbrains.kotlin.psi.KtFile

class FileParsingRule(val config: Config = Config.empty) : MultiRule() {

	override val rules = listOf(MaxLineLength(config))

	override fun visitKtFile(file: KtFile) {
		val lines = file.text.splitToSequence("\n")
		val fileContents = KtFileContent(file, lines)
		fileContents.reportFindings(context, rules)
	}
}

abstract class File(open val file: KtFile)
data class KtFileContent(override val file: KtFile, val content: Sequence<String>) : File(file)

class MaxLineLength(config: Config = Config.empty) : SubRule<KtFileContent>(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Line detected that is longer than the defined maximum line length in the code style.",
			Debt.FIVE_MINS)

	private val maxLineLength: Int
			= valueOrDefault(MaxLineLength.MAX_LINE_LENGTH, MaxLineLength.DEFAULT_IDEA_LINE_LENGTH)
	private val excludePackageStatements: Boolean
			= valueOrDefault(MaxLineLength.EXCLUDE_PACKAGE_STATEMENTS, MaxLineLength.DEFAULT_VALUE_PACKAGE_EXCLUDE)
	private val excludeImportStatements: Boolean
			= valueOrDefault(MaxLineLength.EXCLUDE_IMPORT_STATEMENTS, MaxLineLength.DEFAULT_VALUE_IMPORTS_EXCLUDE)

	override fun apply(element: KtFileContent) {
		var offset = 0
		val lines = element.content
		val file = element.file
		lines.filter { filterPackageStatements(it) }
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
			return !line.trim().startsWith("package ")
		}
		return true
	}

	private fun filterImportStatements(line: String): Boolean {
		if (excludeImportStatements) {
			return !line.trim().startsWith("import ")
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

