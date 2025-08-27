package io.gitlab.arturbosch.detekt.rules.style

import com.intellij.psi.PsiFile
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.Rule
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.api.config
import dev.detekt.psi.absolutePath
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

/**
 * This rule ensures that raw strings have a consistent indentation.
 *
 * The content of a multi line raw string should have the same indentation as the enclosing expression plus the
 * configured indentSize. The closing triple-quotes (`"""`)  must have the same indentation as the enclosing expression.
 *
 * Warning: Rule `MultilineRawStringIndentation` overlaps with `StringTemplateIndent` from the ktlint rule set.
 *
 * <noncompliant>
 * val a = """
 * Hello World!
 * How are you?
 * """.trimMargin()
 *
 * val a = """
 *         Hello World!
 *         How are you?
 *         """.trimMargin()
 * </noncompliant>
 *
 * <compliant>
 * val a = """
 *     Hello World!
 *     How are you?
 * """.trimMargin()
 *
 * val a = """
 *     Hello World!
 *       How are you?
 * """.trimMargin()
 * </compliant>
 */
class MultilineRawStringIndentation(config: Config) : Rule(
    config,
    "The indentation of the raw String should be consistent"
) {

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("allows to provide a list of multiline string trimming methods")
    private val trimmingMethods: List<String> by config(listOf("trimIndent", "trimMargin"))

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)

        if (!expression.isRawStringWithLineBreak() || !expression.isTrimmed(trimmingMethods)) return

        if (!expression.isSurroundedByLineBreaks()) {
            report(
                Finding(
                    Entity.from(expression),
                    "A multiline raw string should start with a break line and should end with another",
                )
            )
            return
        }

        val lineAndColumn = DiagnosticUtils.getLineAndColumnInPsiFile(expression.containingFile, expression.textRange)
        val lineCount = expression.text.lines().count()

        expression.checkIndentation(
            baseIndent = lineAndColumn.lineContent?.countIndent() ?: return,
            firstLineNumber = lineAndColumn.line,
            lastLineNumber = lineAndColumn.line + lineCount - 1
        )
    }

    private fun KtStringTemplateExpression.checkIndentation(
        baseIndent: Int,
        firstLineNumber: Int,
        lastLineNumber: Int,
    ) {
        checkContent(desiredIndent = baseIndent + indentSize, (firstLineNumber + 1)..(lastLineNumber - 1))
        checkClosing(baseIndent, lastLineNumber)
    }

    private fun KtStringTemplateExpression.checkContent(
        desiredIndent: Int,
        lineNumberRange: IntRange,
    ) {
        data class LineInformation(val lineNumber: Int, val line: String, val currentIndent: Int)

        val indentation = lineNumberRange
            .map { lineNumber ->
                val line = containingFile.getLine(lineNumber)
                LineInformation(lineNumber, line, line.countIndent())
            }

        if (indentation.isNotEmpty()) {
            indentation
                .filter { (_, line, currentIndent) -> line.isNotEmpty() && currentIndent < desiredIndent }
                .onEach { (lineNumber, line, currentIndent) ->
                    val location = containingFile.getLocation(
                        SourceLocation(lineNumber, if (line.isBlank()) 1 else currentIndent + 1),
                        SourceLocation(lineNumber, line.length + 1)
                    )

                    report(this, location, message(desiredIndent, currentIndent))
                }
                .ifEmpty {
                    if (indentation.none { (_, _, currentIndent) -> currentIndent == desiredIndent }) {
                        val location = containingFile.getLocation(
                            SourceLocation(lineNumberRange.first, desiredIndent + 1),
                            SourceLocation(lineNumberRange.last, indentation.last().line.length + 1),
                        )

                        report(
                            this,
                            location,
                            message(desiredIndent, indentation.minOf { (_, _, indent) -> indent }),
                        )
                    }
                }
        }
    }

    private fun KtStringTemplateExpression.checkClosing(
        desiredIndent: Int,
        lineNumber: Int,
    ) {
        val currentIndent = containingFile.getLine(lineNumber).countIndent()
        if (currentIndent != desiredIndent) {
            val location = if (currentIndent < desiredIndent) {
                containingFile.getLocation(
                    SourceLocation(lineNumber, currentIndent + 1),
                    SourceLocation(lineNumber, currentIndent + "\"\"\"".length + 1),
                )
            } else {
                containingFile.getLocation(
                    SourceLocation(lineNumber, desiredIndent + 1),
                    SourceLocation(lineNumber, currentIndent + 1),
                )
            }

            report(this, location, message(desiredIndent, currentIndent))
        }
    }
}

private fun Rule.report(element: KtElement, location: Location, message: String) {
    report(Finding(Entity.from(element, location), message))
}

private fun message(desiredIntent: Int, currentIndent: Int): String =
    "The indentation should be $desiredIntent but it is $currentIndent."

private fun KtStringTemplateExpression.isSurroundedByLineBreaks(): Boolean {
    val entries = this.entries
    return entries.takeWhile { it.isBlankOrLineBreak() }.any { it.text == "\n" } &&
        entries.takeLastWhile { it.isBlankOrLineBreak() }.any { it.text == "\n" }
}

private fun KtStringTemplateEntry.isBlankOrLineBreak(): Boolean {
    val text = (this as? KtLiteralStringTemplateEntry)?.text ?: return false
    return text.all { it.isTabChar() } || text == "\n"
}

private fun Char.isTabChar() = this == ' ' || this == '\t'

private fun String.countIndent() = this.takeWhile { it.isTabChar() }.count()

private fun PsiFile.getLine(line: Int): String = text.lineSequence().drop(line - 1).first()

private fun PsiFile.getLocation(start: SourceLocation, end: SourceLocation): Location {
    val lines = this.text.lines()
    var startOffset = 0
    for (i in 1..<start.line) {
        startOffset += lines[i - 1].length + 1
    }
    var endOffset = startOffset
    for (i in start.line..<end.line) {
        endOffset += lines[i - 1].length + 1
    }
    this.text.lines()
    return Location(
        start,
        end,
        TextLocation(startOffset + start.column - 1, endOffset + end.column - 1),
        absolutePath()
    )
}
