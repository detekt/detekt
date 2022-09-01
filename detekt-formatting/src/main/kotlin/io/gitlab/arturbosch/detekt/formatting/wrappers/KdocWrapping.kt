package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.experimental.KdocWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#kdoc-wrapping) for documentation.
 */
@AutoCorrectable(since = "1.20.0")
class KdocWrapping(config: Config) : FormattingRule(config) {

    override val wrapping = KdocWrappingRule()
    override val issue = issueFor("Reports mis-indented code")

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            DefaultEditorConfigProperties.indentSizeProperty to indentSize.toString(),
        )
}
