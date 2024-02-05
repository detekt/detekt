package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.lastArgumentMatchesUrl
import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * This rule validates the end of the first sentence of a KDoc comment.
 * It should end with proper punctuation or with a correct URL.
 */
@Suppress("MemberNameEqualsClassName")
class EndOfSentenceFormat(config: Config) : Rule(
    config,
    "The first sentence in a KDoc comment should end with proper punctuation or with a correct URL."
) {

    @Configuration("regular expression which should match the end of the first sentence in the KDoc")
    private val endOfSentenceFormat: Regex by config("""([.?!][ \t\n\r\f<])|([.?!:]$)""") { it.toRegex() }

    private val htmlTag = Regex("^<.+>")

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        dcl.docComment?.let {
            val text = it.getDefaultSection().getContent()
            if (text.isEmpty() || text.startsWithHtmlTag()) {
                return
            }
            if (!endOfSentenceFormat.containsMatchIn(text) && !text.lastArgumentMatchesUrl()) {
                report(
                    CodeSmell(
                        Entity.from(it.getDefaultSection()),
                        "The first sentence of this KDoc does not end with the correct punctuation."
                    )
                )
            }
        }
    }

    private fun String.startsWithHtmlTag() = startsWith("<") && contains(htmlTag)
}
