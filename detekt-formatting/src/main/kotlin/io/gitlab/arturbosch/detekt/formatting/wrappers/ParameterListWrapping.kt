package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.ParameterListWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#parameter-list-wrapping) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class ParameterListWrapping(config: Config) : FormattingRule(config) {

    override val wrapping = ParameterListWrappingRule()
    override val issue = issueFor("Detects mis-aligned parameter lists")

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    @Configuration("indentation size")
    @Deprecated("`indentSize` is ignored by KtLint and will have no effect")
    @Suppress("UnusedPrivateMember")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            DefaultEditorConfigProperties.maxLineLengthProperty to maxLineLength.toString()
        )
}
