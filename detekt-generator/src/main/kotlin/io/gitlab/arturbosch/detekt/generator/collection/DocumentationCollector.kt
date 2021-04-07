package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidCodeExampleDocumentationException
import org.jetbrains.kotlin.psi.KtClassOrObject

class DocumentationCollector {

    private var name: String = ""
    var description: String = ""
        private set
    var compliant: String = ""
        private set
    var nonCompliant: String = ""
        private set

    fun setClass(classOrObject: KtClassOrObject) {
        name = classOrObject.name?.trim() ?: ""
        classOrObject.kDocSection()
            ?.getContent()
            ?.trim()
            ?.replace("@@", "@")
            ?.let(::extractRuleDocumentation)
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
