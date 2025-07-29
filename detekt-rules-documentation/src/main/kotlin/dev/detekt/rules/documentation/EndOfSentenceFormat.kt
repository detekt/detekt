package dev.detekt.rules.documentation

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
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
                    Finding(
                        Entity.from(it.getDefaultSection()),
                        "The first sentence of this KDoc does not end with the correct punctuation."
                    )
                )
            }
        }
    }

    private fun String.startsWithHtmlTag() = startsWith("<") && contains(htmlTag)
}
