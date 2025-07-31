package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.TrailingCommaOnCallSiteRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.configWithAndroidVariants
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#trailing-comma-on-call-site) for documentation.
 *
 * The default config comes from ktlint and follows these conventions:
 * - [Kotlin coding convention](https://kotlinlang.org/docs/coding-conventions.html#trailing-commas) recommends
 * trailing comma encourage the use of trailing commas at the declaration site and
 * leaves it at your discretion for the call site.
 * - [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide) does not include
 * trailing comma usage yet.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "1.22.0")
class TrailingCommaOnCallSite(config: Config) : FormattingRule(
    config,
    "Rule to mandate/forbid trailing commas at call sites"
) {

    override val wrapping = TrailingCommaOnCallSiteRule()

    @Configuration("Defines whether trailing commas are required (true) or forbidden (false) at call sites")
    private val useTrailingCommaOnCallSite by configWithAndroidVariants(
        defaultValue = true,
        defaultAndroidValue = false,
    )

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            TrailingCommaOnCallSiteRule.TRAILING_COMMA_ON_CALL_SITE_PROPERTY to useTrailingCommaOnCallSite.toString(),
        )
}
