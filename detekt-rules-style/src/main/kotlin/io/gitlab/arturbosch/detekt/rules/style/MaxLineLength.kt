package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.lastArgumentMatchesUrl
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * This rule reports lines of code which exceed a defined maximum line length.
 *
 * Long lines might be hard to read on smaller screens or printouts. Additionally, having a maximum line length
 * in the codebase will help make the code more uniform.
 */
@ActiveByDefault(since = "1.0.0")
class MaxLineLength(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Line detected, which is longer than the defined maximum line length in the code style.",
        Debt.FIVE_MINS
    )

    @Suppress("MemberNameEqualsClassName")
    @Configuration("maximum line length")
    private val maxLineLength: Int by config(DEFAULT_IDEA_LINE_LENGTH)

    @Configuration("if package statements should be ignored")
    private val excludePackageStatements: Boolean by config(true)

    @Configuration("if import statements should be ignored")
    private val excludeImportStatements: Boolean by config(true)

    @Configuration("if comment statements should be ignored")
    private val excludeCommentStatements: Boolean by config(false)

    @Configuration("if raw strings should be ignored")
    private val excludeRawStrings: Boolean by config(true)

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        visit(file.toFileContent())
    }

    private fun visit(element: KtFileContent) {
        var offset = 0
        val lines = element.content
        val file = element.file

        for (line in lines) {
            offset += line.length
            if (!isValidLine(file, offset, line)) {
                val ktElement = findFirstMeaningfulKtElementInParents(file, offset, line) ?: file
                val location = Location.from(file, offset - line.length).let { location ->
                    Location(
                        source = location.source,
                        endSource = SourceLocation(location.source.line, line.length + 1),
                        text = TextLocation(offset - line.length, offset),
                        filePath = location.filePath,
                    )
                }
                report(CodeSmell(issue, Entity.from(ktElement, location), issue.description))
            }

            offset += 1 /* '\n' */
        }
    }

    private fun isValidLine(file: KtFile, offset: Int, line: String): Boolean {
        val isUrl = line.lastArgumentMatchesUrl()
        return line.length <= maxLineLength || isIgnoredStatement(file, offset, line) || isUrl
    }

    private fun isIgnoredStatement(file: KtFile, offset: Int, line: String): Boolean {
        return containsIgnoredPackageStatement(line) ||
            containsIgnoredImportStatement(line) ||
            containsIgnoredCommentStatement(line) ||
            containsIgnoredRawString(file, offset, line)
    }

    private fun containsIgnoredRawString(file: KtFile, offset: Int, line: String): Boolean {
        if (!excludeRawStrings) return false

        return findKtElementInParents(file, offset, line).lastOrNull()?.isInsideRawString() == true
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
        private const val DEFAULT_IDEA_LINE_LENGTH = 120
        private val BLANK_OR_QUOTES = """[\s"]*""".toRegex()

        private fun findFirstMeaningfulKtElementInParents(file: KtFile, offset: Int, line: String): PsiElement? {
            return findKtElementInParents(file, offset, line)
                .firstOrNull { !BLANK_OR_QUOTES.matches(it.text) }
        }
    }
}

private fun PsiElement.isInsideRawString(): Boolean {
    return this is KtStringTemplateExpression || getParentOfType<KtStringTemplateExpression>(false) != null
}
