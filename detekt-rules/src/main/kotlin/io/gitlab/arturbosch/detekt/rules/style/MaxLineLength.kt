package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.lastArgumentMatchesUrl
import org.jetbrains.kotlin.psi.KtFile

data class KtFileContent(val file: KtFile, val content: Sequence<String>)

/**
 * This rule reports lines of code which exceed a defined maximum line length.
 *
 * Long lines might be hard to read on smaller screens or printouts. Additionally having a maximum line length
 * in the codebase will help make the code more uniform.
 *
 * @configuration maxLineLength - maximum line length (default: `120`)
 * @configuration excludePackageStatements - if package statements should be ignored (default: `true`)
 * @configuration excludeImportStatements - if import statements should be ignored (default: `true`)
 * @configuration excludeCommentStatements - if comment statements should be ignored (default: `false`)
 *
 * @active since v1.0.0
 */
class MaxLineLength(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Line detected that is longer than the defined maximum line length in the code style.",
            Debt.FIVE_MINS)

    private val lengthThreshold: Int =
            valueOrDefault(MAX_LINE_LENGTH, DEFAULT_IDEA_LINE_LENGTH)
    private val excludePackageStatements: Boolean =
            valueOrDefault(EXCLUDE_PACKAGE_STATEMENTS, true)
    private val excludeImportStatements: Boolean =
            valueOrDefault(EXCLUDE_IMPORT_STATEMENTS, true)
    private val excludeCommentStatements: Boolean =
            valueOrDefault(EXCLUDE_COMMENT_STATEMENTS, false)

    fun visit(element: KtFileContent) {
        var offset = 0
        val lines = element.content
        val file = element.file

        for (line in lines) {
            offset += line.length
            if (!isValidLine(line)) {
                val ktElement = findFirstKtElementInParents(file, offset, line)
                if (ktElement != null) {
                    report(CodeSmell(issue, Entity.from(ktElement), issue.description))
                } else {
                    report(CodeSmell(issue, Entity.from(file, offset), issue.description))
                }
            }

            offset += 1 /* '\n' */
        }
    }

    private fun isValidLine(line: String): Boolean {
        val isUrl = line.lastArgumentMatchesUrl()
        return line.length <= lengthThreshold || isIgnoredStatement(line) || isUrl
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
        if (!excludeImportStatements) return false

        return line.trimStart().startsWith("import ")
    }

    private fun containsIgnoredCommentStatement(line: String): Boolean {
        if (!excludeCommentStatements) return false

        return line.trimStart().startsWith("//") ||
                line.trimStart().startsWith("/*") ||
                line.trimStart().startsWith("*")
    }

    companion object {
        const val MAX_LINE_LENGTH = "maxLineLength"
        const val DEFAULT_IDEA_LINE_LENGTH = 120
        const val EXCLUDE_PACKAGE_STATEMENTS = "excludePackageStatements"
        const val EXCLUDE_IMPORT_STATEMENTS = "excludeImportStatements"
        const val EXCLUDE_COMMENT_STATEMENTS = "excludeCommentStatements"
    }
}
