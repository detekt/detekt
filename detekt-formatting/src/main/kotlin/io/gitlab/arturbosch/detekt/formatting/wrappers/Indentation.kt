package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.IndentationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.config
import io.gitlab.arturbosch.detekt.formatting.CONTINUATION_INDENT_SIZE_KEY
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.INDENT_SIZE_KEY

/**
 * See <a href="https://ktlint.github.io/#rule-indentation">ktlint-website</a> for documentation.
 */
@AutoCorrectable(since = "1.0.0")
class Indentation(config: Config) : FormattingRule(config) {

    override val wrapping = IndentationRule()
    override val issue = issueFor("Reports mis-indented code")

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("continuation indentation size")
    private val continuationIndentSize by config(4)

    override fun overrideEditorConfig() = mapOf(
        INDENT_SIZE_KEY to indentSize,
        CONTINUATION_INDENT_SIZE_KEY to continuationIndentSize
    )
}
