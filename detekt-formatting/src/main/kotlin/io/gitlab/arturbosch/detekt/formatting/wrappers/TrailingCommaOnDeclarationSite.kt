package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.TrailingCommaOnDeclarationSiteRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/) for documentation.
 */
@AutoCorrectable(since = "1.22.0")
@ActiveByDefault(since = "1.22.0")
class TrailingCommaOnDeclarationSite(config: Config) : FormattingRule(config) {

    override val wrapping = TrailingCommaOnDeclarationSiteRule()
    override val issue = issueFor("Rule to mandate/forbid trailing commas at declaration sites")

    @Configuration("Defines whether a trailing comma (or no trailing comma) should be enforced at declaration sites")
    private val useTrailingCommaOnDeclarationSite by config(false)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            TrailingCommaOnDeclarationSiteRule.allowTrailingCommaProperty to
                useTrailingCommaOnDeclarationSite.toString()
        )
}
