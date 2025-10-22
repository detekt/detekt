package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.MAX_LINE_LENGTH_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.ParameterListSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule
import dev.detekt.rules.ktlintwrapper.configWithAndroidVariants

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#parameter-list-spacing) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "1.22.0")
class ParameterListSpacing(config: Config) : KtlintRule(
    config,
    "Ensure consistent spacing inside the parameter list."
) {

    override val wrapping = ParameterListSpacingRule()

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            MAX_LINE_LENGTH_PROPERTY to maxLineLength.toString()
        )
}
