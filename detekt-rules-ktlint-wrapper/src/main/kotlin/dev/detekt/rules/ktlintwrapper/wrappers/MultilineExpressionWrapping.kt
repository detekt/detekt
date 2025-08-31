package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.FunctionSignatureRule.Companion.FUNCTION_BODY_EXPRESSION_WRAPPING_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.MultilineExpressionWrappingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#multiline-expression-wrapping) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "1.23.0")
class MultilineExpressionWrapping(config: Config) : KtlintRule(
    config,
    "Multiline expression on the right hand side of an expression must start on a separate line."
) {

    override val wrapping = MultilineExpressionWrappingRule()

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("function body expression wrapping")
    private val functionBodyExpressionWrapping by config("multiline")

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            FUNCTION_BODY_EXPRESSION_WRAPPING_PROPERTY to functionBodyExpressionWrapping.toString(),
            INDENT_SIZE_PROPERTY to indentSize.toString(),
        )
}
