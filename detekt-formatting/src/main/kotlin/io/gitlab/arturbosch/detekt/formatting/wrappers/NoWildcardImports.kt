package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.NoWildcardImportsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#no-wildcard-imports) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
class NoWildcardImports(config: Config) : FormattingRule(config) {

    override val wrapping = NoWildcardImportsRule()
    override val issue = issueFor("Detects wildcard imports")

    @Configuration("Defines allowed wildcard imports")
    private val packagesToUseImportOnDemandProperty by config(ALLOWED_WILDCARD_IMPORTS)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            NoWildcardImportsRule.IJ_KOTLIN_PACKAGES_TO_USE_IMPORT_ON_DEMAND to packagesToUseImportOnDemandProperty
        )

    companion object {
        private const val ALLOWED_WILDCARD_IMPORTS = "java.util.*,kotlinx.android.synthetic.**"
    }
}
