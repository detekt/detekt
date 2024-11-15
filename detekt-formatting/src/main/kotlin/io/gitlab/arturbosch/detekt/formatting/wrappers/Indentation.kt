package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.IndentationRule
import com.pinterest.ktlint.ruleset.standard.rules.IndentationRule.Companion.INDENT_WHEN_ARROW_ON_NEW_LINE
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#indentation) for documentation.
 */
@ActiveByDefault(since = "1.19.0")
@AutoCorrectable(since = "1.0.0")
class Indentation(config: Config) : FormattingRule(
    config,
    "Reports mis-indented code"
) {

    override val wrapping = IndentationRule()

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("indent when arrow on new line")
    private val indentWhenArrowOnNewLine by config(false)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            INDENT_SIZE_PROPERTY to indentSize.toString(),
            INDENT_WHEN_ARROW_ON_NEW_LINE to indentWhenArrowOnNewLine.toString(),
        )
}
