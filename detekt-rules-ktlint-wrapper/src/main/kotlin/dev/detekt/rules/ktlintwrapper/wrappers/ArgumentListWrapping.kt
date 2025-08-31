package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.MAX_LINE_LENGTH_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.ArgumentListWrappingRule
import com.pinterest.ktlint.ruleset.standard.rules.ArgumentListWrappingRule.Companion.IGNORE_WHEN_PARAMETER_COUNT_GREATER_OR_EQUAL_THAN_PROPERTY
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.api.configWithAndroidVariants
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#argument-list-wrapping) for documentation.
 */
@AutoCorrectable(since = "1.0.0")
@ActiveByDefault(since = "1.22.0")
class ArgumentListWrapping(config: Config) : KtlintRule(
    config,
    "Reports incorrect argument list wrapping"
) {

    override val wrapping = ArgumentListWrappingRule()

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("maximum line length")
    private val maxLineLength by configWithAndroidVariants(120, 100)

    @Configuration("parameter threshold to ignore rule")
    private val ignoreRuleParameterThreshold by config(8)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            INDENT_SIZE_PROPERTY to indentSize.toString(),
            MAX_LINE_LENGTH_PROPERTY to maxLineLength.toString(),
            IGNORE_WHEN_PARAMETER_COUNT_GREATER_OR_EQUAL_THAN_PROPERTY to ignoreRuleParameterThreshold.toString(),
        )
}
