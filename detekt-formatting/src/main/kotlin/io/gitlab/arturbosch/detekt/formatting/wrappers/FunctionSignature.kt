package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.experimental.FunctionSignatureRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.21.0")
class FunctionSignature(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionSignatureRule()
    override val issue = issueFor(
        "Rewrites the function signature to a single line when possible or a multiline signature otherwise."
    )

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("maximum line length")
    private val maxLineLength by configWithAndroidVariants(120, 100)

    @Configuration("force multiline when parameter count greater or equal than")
    private val forceMultilineWhenParameterCountGreaterOrEqualThan by config("unset")

    @Configuration("function body expression wrapping")
    private val functionBodyExpressionWrapping by config("default")

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            DefaultEditorConfigProperties.indentSizeProperty to indentSize.toString(),
            DefaultEditorConfigProperties.maxLineLengthProperty to maxLineLength.toString(),
            FunctionSignatureRule.forceMultilineWhenParameterCountGreaterOrEqualThanProperty to forceMultilineWhenParameterCountGreaterOrEqualThan,
            FunctionSignatureRule.functionBodyExpressionWrappingProperty to functionBodyExpressionWrapping,
        )
}
