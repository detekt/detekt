package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.ArgumentListWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.ANDROID_MAX_LINE_LENGTH
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_IDEA_LINE_LENGTH
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_INDENT
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.INDENT_SIZE_KEY
import io.gitlab.arturbosch.detekt.formatting.MAX_LINE_LENGTH_KEY

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @configuration indentSize - indentation size (default: `4`)
 * @configuration maxLineLength - maximum line length (default: `120`)
 */
@AutoCorrectable(since = "1.0.0")
class ArgumentListWrapping(config: Config) : FormattingRule(config) {

    override val wrapping = ArgumentListWrappingRule()
    override val issue = issueFor("Reports incorrect argument list wrapping")

    private val indentSize = valueOrDefault(INDENT_SIZE, DEFAULT_INDENT)
    private val defaultMaxLineLength = if (isAndroid) ANDROID_MAX_LINE_LENGTH else DEFAULT_IDEA_LINE_LENGTH
    private val maxLineLength = valueOrDefault(MAX_LINE_LENGTH, defaultMaxLineLength)

    override fun overrideEditorConfig() = mapOf(
        INDENT_SIZE_KEY to indentSize,
        MAX_LINE_LENGTH_KEY to maxLineLength
    )

    companion object {
        const val INDENT_SIZE = "indentSize"
        const val MAX_LINE_LENGTH = "maxLineLength"
    }
}
