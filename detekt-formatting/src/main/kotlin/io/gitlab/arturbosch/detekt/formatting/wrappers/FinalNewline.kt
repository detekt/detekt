package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.EditorConfig
import com.pinterest.ktlint.ruleset.standard.FinalNewlineRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.merge

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @configuration insertFinalNewLine - report absence or presence of a newline (default: `true`)
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 *
 */
class FinalNewline(config: Config) : FormattingRule(config) {

    override val wrapping = FinalNewlineRule()
    override val issue = issueFor("Detects missing final newlines")

    private val insertFinalNewline = valueOrDefault(INSERT_FINAL_NEWLINE, true)

    override fun editorConfigUpdater(): ((oldEditorConfig: EditorConfig?) -> EditorConfig)? = {
        EditorConfig.merge(it, insertFinalNewline = insertFinalNewline)
    }
}

const val INSERT_FINAL_NEWLINE = "insertFinalNewline"
