package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.ruleset.standard.WrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#wrapping) for documentation.
 */
@ActiveByDefault(since = "1.20.0")
@AutoCorrectable(since = "1.20.0")
class Wrapping(config: Config) : FormattingRule(config) {

    override val wrapping = WrappingRule()
    override val issue = issueFor("Reports missing newlines (e.g. between parentheses of a multi-line function call")

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            INDENT_SIZE_PROPERTY to indentSize.toString(),
        )
}
