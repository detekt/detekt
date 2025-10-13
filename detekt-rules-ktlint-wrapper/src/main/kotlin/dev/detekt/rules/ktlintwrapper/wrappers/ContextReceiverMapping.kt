package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.MAX_LINE_LENGTH_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.ContextReceiverWrappingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule
import dev.detekt.rules.ktlintwrapper.configWithAndroidVariants

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#content-receiver-wrapping) for documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "1.23.0")
class ContextReceiverMapping(config: Config) : KtlintRule(
    config,
    "Reports mis-indented code"
) {

    override val wrapping = ContextReceiverWrappingRule()

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            MAX_LINE_LENGTH_PROPERTY to maxLineLength.toString(),
            INDENT_SIZE_PROPERTY to indentSize.toString(),
        )
}
