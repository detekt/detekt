package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.rule.engine.core.api.editorconfig.EditorConfigProperty
import io.github.ktlint.core.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import io.github.ktlint.core.ruleset.standard.rules.ExpressionOperandWrappingRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/experimental/#expression-operand-wrapping) for
 * documentation.
 */
internal class ExpressionOperandWrapping(config: Config) :
    KtlintRule(config, "Wraps each operand in a multiline expression to a separate line"),
    AutoCorrectable {

    override val wrapping = ExpressionOperandWrappingRule()

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            INDENT_SIZE_PROPERTY to indentSize.toString(),
        )
}
