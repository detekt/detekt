package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.ArgumentListWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.config
import io.gitlab.arturbosch.detekt.api.internal.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.INDENT_SIZE_KEY
import io.gitlab.arturbosch.detekt.formatting.MAX_LINE_LENGTH_KEY

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 */
@AutoCorrectable(since = "1.0.0")
class ArgumentListWrapping(config: Config) : FormattingRule(config) {

    override val wrapping = ArgumentListWrappingRule()
    override val issue = issueFor("Reports incorrect argument list wrapping")

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("maximum line length")
    private val maxLineLength by configWithAndroidVariants(120, 100)

    override fun overrideEditorConfig() = mapOf(
        INDENT_SIZE_KEY to indentSize,
        MAX_LINE_LENGTH_KEY to maxLineLength
    )
}
