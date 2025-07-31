package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.NoWildcardImportsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-wildcard-imports) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
class NoWildcardImports(config: Config) : FormattingRule(
    config,
    "Detects wildcard imports"
) {

    override val wrapping = NoWildcardImportsRule()

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
