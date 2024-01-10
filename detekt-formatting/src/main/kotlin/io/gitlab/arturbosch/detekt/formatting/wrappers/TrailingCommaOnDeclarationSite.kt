package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.TrailingCommaOnDeclarationSiteRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#trailing-comma-on-declaration-site) for documentation.
 *
 * The default config comes from ktlint and follows these conventions:
 * - [Kotlin coding convention](https://kotlinlang.org/docs/coding-conventions.html#trailing-commas) recommends
 * trailing comma encourage the use of trailing commas at the declaration site and
 * leaves it at your discretion for the call site.
 * - [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide) does not include
 * trailing comma usage yet.
 */
@AutoCorrectable(since = "1.22.0")
class TrailingCommaOnDeclarationSite(config: Config) : FormattingRule(
    config,
    "Rule to mandate/forbid trailing commas at declaration sites"
) {

    override val wrapping = TrailingCommaOnDeclarationSiteRule()

    @Configuration("Defines whether trailing commas are required (true) or forbidden (false) at declaration sites")
    private val useTrailingCommaOnDeclarationSite by configWithAndroidVariants(
        defaultValue = true,
        defaultAndroidValue = false,
    )

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            TrailingCommaOnDeclarationSiteRule.TRAILING_COMMA_ON_DECLARATION_SITE_PROPERTY to
                useTrailingCommaOnDeclarationSite.toString(),
        )
}
