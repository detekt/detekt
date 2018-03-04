package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
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
 * (default: ([.?!][ \t\n\r\f<])|([.?!]$))
 *
 *  @author Marvin Ramin
 *  @author schalkms
 */
class EndOfSentenceFormat(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Maintainability,
			"The first sentence in a KDoc comment should end with correct punctuation.")

	private val endOfSentenceFormat =
			Regex(valueOrDefault(END_OF_SENTENCE_FORMAT, "([.?!][ \\t\\n\\r\\f<])|([.?!]\$)"))

	// https://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url
	private val urlFormat = Regex("[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&//=]*)")
	private val htmlTag = Regex("<.+>")

	fun verify(declaration: KtDeclaration) {
		declaration.docComment?.let {
			val text = it.getDefaultSection().getContent()
			if (text.isEmpty() || text.startsWithHtmlTag()) {
				return
			}
			if (!endOfSentenceFormat.containsMatchIn(text) && !lastArgumentMatchesUrl(text)) {
				report(CodeSmell(issue, Entity.from(declaration),
						"The first sentence of this KDoc does not end with the correct punctuation."))
			}
		}
	}

	private fun String.startsWithHtmlTag() = startsWith("<") && contains(htmlTag)

	private fun lastArgumentMatchesUrl(text: String): Boolean {
		val arguments = text.trimEnd().split(Regex("\\s+"))
		return urlFormat.containsMatchIn(arguments.last())
	}

	companion object {
		const val END_OF_SENTENCE_FORMAT = "endOfSentenceFormat"
	}
}
