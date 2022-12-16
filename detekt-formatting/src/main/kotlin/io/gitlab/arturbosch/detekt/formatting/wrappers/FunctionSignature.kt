package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties
import com.pinterest.ktlint.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.experimental.FunctionSignatureRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#function-signature) for
 * documentation.
 */
@AutoCorrectable(since = "1.22.0")
class FunctionSignature(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionSignatureRule()
    override val issue = issueFor("Format signature to be single when possible, multiple lines otherwise.")

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
            FunctionSignatureRule.forceMultilineWhenParameterCountGreaterOrEqualThanProperty to
                forceMultilineWhenParameterCountGreaterOrEqualThan.toString(),
            FunctionSignatureRule.functionBodyExpressionWrappingProperty to functionBodyExpressionWrapping,
            DefaultEditorConfigProperties.maxLineLengthProperty to maxLineLength.toString(),
            DefaultEditorConfigProperties.indentSizeProperty to indentSize.toString(),
        )
}
