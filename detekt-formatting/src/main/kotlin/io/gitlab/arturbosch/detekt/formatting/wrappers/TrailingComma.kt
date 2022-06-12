package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.experimental.trailingcomma.TrailingCommaRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@OptIn(FeatureInAlphaState::class)
@AutoCorrectable(since = "1.20.0")
class TrailingComma(config: Config) : FormattingRule(config) {

    override val wrapping = TrailingCommaRule()
    override val issue = issueFor("Rule to mandate/forbid trailing commas")

    @Configuration("Defines whether a trailing comma (or no trailing comma) should be enforced on the defining side")
    private val allowTrailingComma by config(false)

    @Configuration("Defines whether a trailing comma (or no trailing comma) should be enforced on the calling side")
    private val allowTrailingCommaOnCallSite by config(false)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            TrailingCommaRule.allowTrailingCommaProperty to allowTrailingComma.toString(),
            TrailingCommaRule.allowTrailingCommaOnCallSiteProperty to allowTrailingCommaOnCallSite.toString(),
        )
}
