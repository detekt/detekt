package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile

class FileParsingRule(val config: Config = Config.empty) : MultiRule() {

	private val maxLineLength = MaxLineLength(config)
	private val trailingWhitespace = TrailingWhitespace(config)
	private val noTabs = NoTabs(config)
	override val rules = listOf(maxLineLength, trailingWhitespace, noTabs)

	override fun visitKtFile(file: KtFile) {
		val lines = file.text.splitToSequence("\n")
		val fileContents = KtFileContent(file, lines)

		maxLineLength.runIfActive { visit(fileContents) }
		trailingWhitespace.runIfActive { visit(fileContents) }
		noTabs.runIfActive { visit(fileContents) }
	}
}

data class KtFileContent(val file: KtFile, val content: Sequence<String>)

/**
 * This rule reports lines of code which exceed a defined maximum line length.
 *
 * Long lines might be hard to read on smaller screens or printouts. Additionally having a maximum line length
 * in the codebase will help make the code more uniform.
 *
 * @configuration maxLineLength - maximum line length (default: 120)
 * @configuration excludePackageStatements - if package statements should be ignored (default: false)
 * @configuration excludeImportStatements - if import statements should be ignored (default: false)
 * @configuration excludeCommentStatements - if comment statements should be ignored (default: false)
 *
 * @active since v1.0.0
 * @author Robbin Voortman
 * @author Marvin Ramin
 * @author Artur Bosch
 */
class MaxLineLength(config: Config = Config.empty) : Rule(config) {

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
	private val excludeCommentStatements: Boolean
			= valueOrDefault(MaxLineLength.EXCLUDE_COMMENT_STATEMENTS, MaxLineLength.DEFAULT_VALUE_COMMENT_EXCLUDE)

	fun visit(element: KtFileContent) {
		var offset = 0
		val lines = element.content
		val file = element.file

		lines.forEach { line ->
			offset += line.length
			if (!isValidLine(line)) {
				report(CodeSmell(issue, Entity.from(file, offset), issue.description))
			}

			offset += 1 /* '\n' */
		}
	}

	private fun isValidLine(line: String): Boolean {
		return (line.length <= maxLineLength || isIgnoredStatement(line))
	}

	private fun isIgnoredStatement(line: String): Boolean {
		return containsIgnoredPackageStatement(line) ||
				containsIgnoredImportStatement(line) ||
				containsIgnoredCommentStatement(line)
	}

	private fun containsIgnoredPackageStatement(line: String): Boolean {
		if (!excludePackageStatements) return false

		return line.trimStart().startsWith("package ")
	}

	private fun containsIgnoredImportStatement(line: String): Boolean {
		if(!excludeImportStatements) return false

		return line.trimStart().startsWith("import ")
	}

	private fun containsIgnoredCommentStatement(line: String): Boolean {
		if (!excludeCommentStatements) return false

		return  line.trimStart().startsWith("//") ||
				line.trimStart().startsWith("/*") ||
				line.trimStart().startsWith("*")
	}

	companion object {
		const val MAX_LINE_LENGTH = "maxLineLength"
		const val DEFAULT_IDEA_LINE_LENGTH = 120

		const val EXCLUDE_PACKAGE_STATEMENTS = "excludePackageStatements"
		const val DEFAULT_VALUE_PACKAGE_EXCLUDE = false

		const val EXCLUDE_IMPORT_STATEMENTS = "excludeImportStatements"
		const val DEFAULT_VALUE_IMPORTS_EXCLUDE = false

		const val EXCLUDE_COMMENT_STATEMENTS = "excludeCommentStatements"
		const val DEFAULT_VALUE_COMMENT_EXCLUDE = false
	}
}
