package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties
import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.experimental.CommentWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-indentation) for documentation.
 */
@AutoCorrectable(since = "1.20.0")
class CommentWrapping(config: Config) : FormattingRule(config) {

    override val wrapping = CommentWrappingRule()
    override val issue = issueFor("Reports mis-indented code")

    @Configuration("indentation size")
    private val indentSize by config(4)

    @OptIn(FeatureInAlphaState::class)
    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            DefaultEditorConfigProperties.indentSizeProperty to indentSize.toString(),
        )
}
