package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.lastArgumentMatchesUrl
import org.jetbrains.kotlin.psi.KtDeclaration

class KDocStyle(config: Config = Config.empty) : MultiRule() {

    private val endOfSentenceFormat = EndOfSentenceFormat(config)

    override val rules = listOf(
            endOfSentenceFormat
    )

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        endOfSentenceFormat.verify(dcl)
    }
}

/**
 * This rule validates the end of the first sentence of a KDoc comment.
 * It should end with proper punctuation or with a correct URL.
 *
 * @configuration endOfSentenceFormat - regular expression which should match the end of the first sentence in the KDoc
 * (default: `'([.?!][ \t\n\r\f<])|([.?!:]$)'`)
 */
@Suppress("MemberNameEqualsClassName")
class EndOfSentenceFormat(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Maintainability,
            "The first sentence in a KDoc comment should end with correct punctuation.",
            Debt.FIVE_MINS)

    private val endOfSentenceFormat =
            Regex(valueOrDefault(END_OF_SENTENCE_FORMAT, "([.?!][ \\t\\n\\r\\f<])|([.?!:]\$)"))
    private val htmlTag = Regex("<.+>")

    fun verify(declaration: KtDeclaration) {
        declaration.docComment?.let {
            val text = it.getDefaultSection().getContent()
            if (text.isEmpty() || text.startsWithHtmlTag()) {
                return
            }
            if (!endOfSentenceFormat.containsMatchIn(text) && !text.lastArgumentMatchesUrl()) {
                report(CodeSmell(issue, Entity.from(declaration),
                        "The first sentence of this KDoc does not end with the correct punctuation."))
            }
        }
    }

    private fun String.startsWithHtmlTag() = startsWith("<") && contains(htmlTag)

    companion object {
        const val END_OF_SENTENCE_FORMAT = "endOfSentenceFormat"
    }
}
