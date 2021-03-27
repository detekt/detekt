package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.IndentationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_INDENT
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.INDENT_SIZE_KEY

/**
 * See <a href="https://ktlint.github.io/#rule-indentation">ktlint-website</a> for documentation.
 *
 * @configuration indentSize - indentation size (default: `4`)
 *
 * @autoCorrect since v1.0.0
 */
class Indentation(config: Config) : FormattingRule(config) {

    override val wrapping = IndentationRule()
    override val issue = issueFor("Reports mis-indented code")

    private val indentSize = valueOrDefault(INDENT_SIZE, DEFAULT_INDENT)

    override fun overrideEditorConfig() = mapOf(
        INDENT_SIZE_KEY to indentSize
    )

    companion object {
        const val INDENT_SIZE = "indentSize"
    }
}
