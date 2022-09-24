package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.TrailingCommaOnCallSiteRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/) for documentation.
 */
@AutoCorrectable(since = "1.23.0")
class TrailingCommaOnCallSite(config: Config) : FormattingRule(config) {

    override val wrapping = TrailingCommaOnCallSiteRule()
    override val issue = issueFor("Rule to mandate/forbid trailing commas")

    @Configuration("Defines whether a trailing comma (or no trailing comma) should be enforced at call sites")
    private val allowTrailingComma by config(false)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            TrailingCommaOnCallSiteRule.allowTrailingCommaOnCallSiteProperty to allowTrailingComma.toString(),
        )
}
