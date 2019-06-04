package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity

/**
 * This rule reports lines that end with a whitespace.
 *
 * @author Misa Torres
 * @author schalkms
 */
class TrailingWhitespace(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Checks which lines end with a whitespace.",
            Debt.FIVE_MINS)

    fun visit(fileContent: KtFileContent) {
        var offset = 0
        fileContent.content.forEachIndexed { index, line ->
            offset += line.length
            if (hasTrailingWhitespace(line)) {
                val file = fileContent.file
                val ktElement = findFirstKtElementInParents(file, offset, line)
                if (ktElement != null) {
                    report(CodeSmell(issue, Entity.from(ktElement), createMessage(index)))
                } else {
                    report(CodeSmell(issue, Entity.from(file, offset), createMessage(index)))
                }
            }
            offset += 1 /* '\n' */
        }
    }

    private fun hasTrailingWhitespace(line: String) =
            line.isNotEmpty() && (line.last() == ' ' || line.last() == '\t')

    private fun createMessage(line: Int) = "Line ${line + 1} ends with a whitespace."
}
