package dev.detekt.generator.collection

import dev.detekt.generator.collection.exception.InvalidCodeExampleDocumentationException
import org.jetbrains.kotlin.psi.KtClassOrObject

class DocumentationCollector(private val textReplacements: Map<String, String>) {

    private var name: String = ""
    var description: String = ""
        private set(newValue) {
            field = textReplacements.toList()
                .fold(newValue) { acc, replacement ->
                    acc.replace(replacement.first, replacement.second)
                }
        }
    var compliant: String = ""
        private set
    var nonCompliant: String = ""
        private set

    fun setClass(classOrObject: KtClassOrObject) {
        name = classOrObject.name?.trim().orEmpty()
        classOrObject.docComment
            ?.text
            ?.let(::extractKDocContent)
            ?.trim()
            ?.replace("@@", "@")
            ?.let(::extractRuleDocumentation)
    }

    private fun extractKDocContent(rawKDoc: String): String {
        // Remove the opening /** and closing */
        val withoutDelimiters = rawKDoc
            .removePrefix("/**")
            .removeSuffix("*/")

        // Process each line to remove the leading " * " pattern while preserving indentation after it
        return withoutDelimiters
            .lines()
            .joinToString("\n") { line ->
                // Match the KDoc comment pattern: optional whitespace + asterisk + optional space
                when {
                    line.trimStart().startsWith("* ") -> {
                        // Find the position after " * " and preserve everything after that
                        val asteriskIndex = line.indexOf('*')
                        if (asteriskIndex + 2 < line.length) {
                            line.substring(asteriskIndex + 2)
                        } else {
                            ""
                        }
                    }

                    line.trimStart().startsWith("*") && line.trimStart().length > 1 -> {
                        // Asterisk without space after it
                        val asteriskIndex = line.indexOf('*')
                        if (asteriskIndex + 1 < line.length) {
                            line.substring(asteriskIndex + 1)
                        } else {
                            ""
                        }
                    }

                    // Just an asterisk, empty line
                    line.trimStart() == "*" -> ""

                    else -> line.trimStart() // No asterisk, just trim
                }
            }
    }

    private fun extractRuleDocumentation(comment: String) {
        val nonCompliantIndex = comment.indexOf(TAG_NONCOMPLIANT)
        val compliantIndex = comment.indexOf(TAG_COMPLIANT)
        when {
            nonCompliantIndex != -1 -> {
                extractNonCompliantDocumentation(comment, nonCompliantIndex)
                extractCompliantDocumentation(comment, compliantIndex)
            }

            compliantIndex != -1 -> throw InvalidCodeExampleDocumentationException(
                "Rule $name contains a compliant without a noncompliant code definition"
            )

            else -> description = comment
        }
    }

    private fun extractNonCompliantDocumentation(comment: String, nonCompliantIndex: Int) {
        val nonCompliantEndIndex = comment.indexOf(ENDTAG_NONCOMPLIANT)
        if (nonCompliantEndIndex == -1) {
            throw InvalidCodeExampleDocumentationException(
                "Rule $name contains an incorrect noncompliant code definition"
            )
        }
        description = comment.substring(0, nonCompliantIndex).trim()
        nonCompliant = comment.substring(nonCompliantIndex + TAG_NONCOMPLIANT.length, nonCompliantEndIndex)
            .trimStartingLineBreaks()
            .trimEnd()
    }

    private fun extractCompliantDocumentation(comment: String, compliantIndex: Int) {
        val compliantEndIndex = comment.indexOf(ENDTAG_COMPLIANT)
        if (compliantIndex != -1) {
            if (compliantEndIndex == -1) {
                throw InvalidCodeExampleDocumentationException(
                    "Rule $name contains an incorrect compliant code definition"
                )
            }
            compliant = comment.substring(compliantIndex + TAG_COMPLIANT.length, compliantEndIndex)
                .trimStartingLineBreaks()
                .trimEnd()
        }
    }

    private fun String.trimStartingLineBreaks(): String {
        var i = 0
        while (i < this.length && (this[i] == '\n' || this[i] == '\r')) {
            i++
        }
        return this.substring(i)
    }

    companion object {
        private const val TAG_NONCOMPLIANT = "<noncompliant>"
        private const val ENDTAG_NONCOMPLIANT = "</noncompliant>"
        private const val TAG_COMPLIANT = "<compliant>"
        private const val ENDTAG_COMPLIANT = "</compliant>"
    }
}
