package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.FunctionNamingRule
import com.pinterest.ktlint.ruleset.standard.rules.FunctionNamingRule.Companion.IGNORE_WHEN_ANNOTATED_WITH_PROPERTY
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#function-naming) for
 * documentation.
 */
class FunctionName(config: Config) : KtlintRule(
    config,
    "Function name should start with a lowercase letter (except factory methods) and use camel case."
) {
    override val wrapping = FunctionNamingRule()

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            IGNORE_WHEN_ANNOTATED_WITH_PROPERTY to "",
        )
}
