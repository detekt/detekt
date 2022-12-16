package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties
import com.pinterest.ktlint.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.experimental.ContextReceiverWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#content-receiver-wrapping) for documentation.
 */
@AutoCorrectable(since = "1.23.0")
class ContextReceiverMapping(config: Config) : FormattingRule(config) {

    override val wrapping = ContextReceiverWrappingRule()
    override val issue = issueFor("Reports mis-indented code")

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            DefaultEditorConfigProperties.maxLineLengthProperty to maxLineLength.toString(),
            DefaultEditorConfigProperties.indentSizeProperty to indentSize.toString(),
        )
}
