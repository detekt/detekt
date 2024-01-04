package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.rules.isPartOfString
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
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
class TrailingWhitespace(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "Whitespaces at the end of a line are unnecessary and can be removed.",
    )

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        visit(file.toFileContent())
    }

    private fun visit(fileContent: KtFileContent) {
        var offset = 0
        fileContent.content.forEachIndexed { index, line ->
            offset += line.length
            val trailingWhitespaces = countTrailingWhitespace(line)
            if (trailingWhitespaces > 0) {
                val file = fileContent.file
                val ktElement = findFirstKtElementInParentsOrNull(file, offset, line)
                if (ktElement == null || !ktElement.isPartOfString()) {
                    val entity = Entity.from(file, offset - trailingWhitespaces).let { entity ->
                        Entity(
                            entity.name,
                            entity.signature,
                            location = Location(
                                entity.location.source,
                                entity.location.endSource,
                                TextLocation(entity.location.text.start, offset),
                                entity.location.filePath
                            )
                        )
                    }
                    report(CodeSmell(issue, entity, createMessage(index)))
                }
            }
            offset += 1 // '\n'
        }
    }

    private fun countTrailingWhitespace(line: String): Int {
        return line.length - line.indexOfLast { it != ' ' && it != '\t' } - 1
    }

    private fun createMessage(line: Int) = "Line ${line + 1} ends with a whitespace."

    private fun findFirstKtElementInParentsOrNull(file: KtFile, offset: Int, line: String): PsiElement? {
        return findKtElementInParents(file, offset, line)
            .firstOrNull()
    }
}
