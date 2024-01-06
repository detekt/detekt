package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.MAX_LINE_LENGTH_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.WrappingRule
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#wrapping) for documentation.
 */
@ActiveByDefault(since = "1.20.0")
@AutoCorrectable(since = "1.20.0")
class Wrapping(config: Config) : FormattingRule(config) {

    override val wrapping = WrappingRule()
    override val issue = issueFor("Reports missing newlines (e.g. between parentheses of a multi-line function call")

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            MAX_LINE_LENGTH_PROPERTY to maxLineLength.toString(),
            INDENT_SIZE_PROPERTY to indentSize.toString(),
        )
}
