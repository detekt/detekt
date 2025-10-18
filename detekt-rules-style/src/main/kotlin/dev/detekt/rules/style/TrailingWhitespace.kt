package dev.detekt.rules.style

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.Rule
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.psi.absolutePath
import dev.detekt.psi.isPartOfString
import org.jetbrains.kotlin.KtPsiSourceFileLinesMapping
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils.getLineAndColumnRangeInPsiFile
import org.jetbrains.kotlin.psi.KtFile

/**
 * This rule reports lines that end with a whitespace.
 *
 * Note: in KDoc comments we use Markdown, so two spaces at the end of lines should be allowed.
 * However, JetBrains haven't implemented this in their flavour of "standard" Markdown yet
 * ([in Dokka](https://github.com/Kotlin/dokka/issues/2823),
 * nor [in KTIJ](https://youtrack.jetbrains.com/issue/KTIJ-6702/KDoc-Dokka-allow-for-newlines-line-breaks-inside-paragraphs)),
 * which means Markdown line-breaks in KDoc are really only trailing whitespace for now.
 */
class TrailingWhitespace(config: Config) : Rule(
    config,
    "Whitespaces at the end of a line are unnecessary and can be removed."
) {

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val sourceFileLinesMapping = KtPsiSourceFileLinesMapping(file)

        file.text.lineSequence().forEachIndexed { index, line ->
            val trailingWhitespaces = countTrailingWhitespace(line)
            if (trailingWhitespaces > 0) {
                val lineEndOffset = sourceFileLinesMapping.getLineStartOffset(index) + line.length
                val ktElement = findFirstKtElementInParentsOrNull(file, lineEndOffset, line)
                if (ktElement == null || !ktElement.isPartOfString()) {
                    val startOffset = lineEndOffset - trailingWhitespaces
                    val textRange = TextRange(startOffset, lineEndOffset)
                    val lineAndColumnRange = getLineAndColumnRangeInPsiFile(file, textRange)
                    val location =
                        Location(
                            source = SourceLocation(lineAndColumnRange.start.line, lineAndColumnRange.start.column),
                            endSource = SourceLocation(lineAndColumnRange.end.line, lineAndColumnRange.end.column),
                            text = TextLocation(startOffset, lineEndOffset),
                            path = file.absolutePath(),
                        )

                    report(Finding(Entity.from(file, location), createMessage(index)))
                }
            }
        }
    }

    private fun countTrailingWhitespace(line: String): Int =
        line.length - line.indexOfLast { it != ' ' && it != '\t' } - 1

    private fun createMessage(line: Int) = "Line ${line + 1} ends with a whitespace."

    private fun findFirstKtElementInParentsOrNull(file: KtFile, offset: Int, line: String): PsiElement? =
        findKtElementInParents(file, offset, line)
            .firstOrNull()
}
