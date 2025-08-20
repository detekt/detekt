package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.WhenEntryBracing
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/#when-entry-bracing) for
 * documentation.
 */
@AutoCorrectable(since = "2.0.0")
class WhenEntryBracing(config: Config) : FormattingRule(
    config,
    "Enforce consistent usages of braces inside the when-statement."
) {

    override val wrapping = WhenEntryBracing()

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            INDENT_SIZE_PROPERTY to indentSize.toString(),
        )
}
