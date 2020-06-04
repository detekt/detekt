package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.EditorConfig
import com.pinterest.ktlint.ruleset.standard.IndentationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.CONTINUATION_INDENT_SIZE_KEY
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_CONTINUATION_INDENT
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_INDENT
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.INDENT_SIZE_KEY
import io.gitlab.arturbosch.detekt.formatting.copy

/**
 * See <a href="https://ktlint.github.io/#rule-indentation">ktlint-website</a> for documentation.
 *
 * @configuration indentSize - indentation size (default: `4`)
 * @configuration continuationIndentSize - continuation indentation size (default: `4`)
 *
 * @autoCorrect since v1.0.0
 */
class Indentation(config: Config) : FormattingRule(config) {

    override val wrapping = IndentationRule()
    override val issue = issueFor("Reports mis-indented code")

    private val indentSize = valueOrDefault(INDENT_SIZE, DEFAULT_INDENT)
    private val continuationIndentSize = valueOrDefault(CONTINUATION_INDENT_SIZE, DEFAULT_CONTINUATION_INDENT)

    override fun editorConfigUpdater(): ((oldEditorConfig: EditorConfig?) -> EditorConfig)? = {
        it.copy(
            INDENT_SIZE_KEY to indentSize,
            CONTINUATION_INDENT_SIZE_KEY to continuationIndentSize)
    }

    companion object {
        const val INDENT_SIZE = "indentSize"
        const val CONTINUATION_INDENT_SIZE = "continuationIndentSize"
    }
}
