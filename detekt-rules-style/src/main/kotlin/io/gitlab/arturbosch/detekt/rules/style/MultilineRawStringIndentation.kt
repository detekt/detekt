package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.psi.getLineAndColumnInPsiFile
import io.github.detekt.psi.toFilePath
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
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

/**
 * This rule ensures that raw strings have a consistent indentation.
 *
 * The content of a multi line raw string should have the same indentation as the enclosing expression plus the
 * configured indentSize. The closing triple-quotes (`"""`)  must have the same indentation as the enclosing expression.
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
class MultilineRawStringIndentation(val config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "The indentation of the raw String should be consistent",
        Debt.FIVE_MINS
    )

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Suppress("ReturnCount")
    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)

        val text = expression.text
        val lineCount = text.lines().count()
        if (lineCount <= 1) return
        if (!expression.isTrimmed()) return
        if (!text.matches(rawStringRegex)) return

        val lineAndColumn = getLineAndColumnInPsiFile(expression.containingFile, expression.textRange) ?: return

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
        val indentation = lineNumberRange
            .map { lineNumber ->
                val line = containingFile.getLine(lineNumber)
                Triple(lineNumber, line, line.countIndent())
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
                            SourceLocation(lineNumberRange.last, indentation.last().second.length + 1),
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
    report(CodeSmell(issue, Entity.from(element, location), message))
}

private fun message(desiredIntent: Int, currentIndent: Int): String {
    return "The indentation should be $desiredIntent but it is $currentIndent."
}

private val rawStringRegex = "\"\"\"\n(.|\n)*\n *\"\"\"".toRegex()

private fun String.countIndent() = this.takeWhile { it == ' ' }.count()

private fun PsiFile.getLine(line: Int): String {
    return text.lineSequence().drop(line - 1).first()
}

private fun PsiFile.getLocation(start: SourceLocation, end: SourceLocation): Location {
    val lines = this.text.lines()
    var startOffset = 0
    for (i in 1 until start.line) {
        startOffset += lines[i - 1].length + 1
    }
    var endOffset = startOffset
    for (i in start.line until end.line) {
        endOffset += lines[i - 1].length + 1
    }
    this.text.lines()
    return Location(
        start,
        end,
        TextLocation(startOffset + start.column - 1, endOffset + end.column - 1),
        toFilePath()
    )
}
