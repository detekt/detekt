package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.MAX_LINE_LENGTH_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.FunctionSignatureRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule
import dev.detekt.rules.ktlintwrapper.configWithAndroidVariants

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#function-signature) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "1.22.0")
class FunctionSignature(config: Config) :
    KtlintRule(config, "Format signature to be single when possible, multiple lines otherwise.") {

    override val wrapping = FunctionSignatureRule()

    @Configuration("parameter count means multiline threshold")
    private val forceMultilineWhenParameterCountGreaterOrEqualThan by config(2_147_483_647)

    @Configuration("indentation size")
    private val functionBodyExpressionWrapping by config("default")

    @Configuration("maximum line length")
    private val maxLineLength by configWithAndroidVariants(120, 100)

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            FunctionSignatureRule.FORCE_MULTILINE_WHEN_PARAMETER_COUNT_GREATER_OR_EQUAL_THAN_PROPERTY to
                forceMultilineWhenParameterCountGreaterOrEqualThan.toString(),
            FunctionSignatureRule.FUNCTION_BODY_EXPRESSION_WRAPPING_PROPERTY to functionBodyExpressionWrapping,
            MAX_LINE_LENGTH_PROPERTY to maxLineLength.toString(),
            INDENT_SIZE_PROPERTY to indentSize.toString(),
        )
}
