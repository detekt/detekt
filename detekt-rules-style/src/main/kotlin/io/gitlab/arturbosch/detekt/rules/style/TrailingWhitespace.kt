package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPartOfString
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

/**
 * This rule reports lines that end with a whitespace.
 */
class TrailingWhitespace(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Whitespaces at the end of a line are unnecessary and can be removed.",
        Debt.FIVE_MINS
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
                        entity.copy(
                            location = entity.location.copy(
                                text = entity.location.text.copy(end = offset)
                            )
                        )
                    }
                    report(CodeSmell(issue, entity, createMessage(index)))
                }
            }
            offset += 1 /* '\n' */
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
