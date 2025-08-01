package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.PropertyNamingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#property-naming) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
class PropertyName(config: Config) : FormattingRule(
    config,
    "Reports incorrect property name."
) {
    override val wrapping = PropertyNamingRule()

    @Configuration("The naming style ('screaming_snake_case', or 'pascal_case') to be applied on constant properties.")
    private val constantNamingStyle by config("screaming_snake_case")

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            PropertyNamingRule.CONSTANT_NAMING_PROPERTY to constantNamingStyle
        )
}
