package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.NoWildcardImportsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-import">ktlint-website</a> for documentation.
 */
@ActiveByDefault(since = "1.0.0")
class NoWildcardImports(config: Config) : FormattingRule(config) {

    override val wrapping = NoWildcardImportsRule()
    override val issue = issueFor("Detects wildcast import usages")

    @Configuration("Defines allowed wildcard imports")
    private val packagesToUseImportOnDemandProperty by config(ALLOWED_WILDCARD_IMPORTS)

    @OptIn(FeatureInAlphaState::class)
    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            NoWildcardImportsRule.packagesToUseImportOnDemandProperty to packagesToUseImportOnDemandProperty
        )

    companion object {
        private const val ALLOWED_WILDCARD_IMPORTS = "java.util.*,kotlinx.android.synthetic.**"
    }
}
