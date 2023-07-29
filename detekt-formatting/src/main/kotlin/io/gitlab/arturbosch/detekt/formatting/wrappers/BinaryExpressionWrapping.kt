package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.MAX_LINE_LENGTH_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.BinaryExpressionWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/#binary-expression-wrapping) for
 * documentation.
 */
@AutoCorrectable(since = "2.0.0")
class BinaryExpressionWrapping(config: Config) : FormattingRule(config) {

    override val wrapping = BinaryExpressionWrappingRule()
    override val issue =
        issueFor("Wrap binary expression at the operator reference if the binary expression does not fit on the line")

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            INDENT_SIZE_PROPERTY to indentSize.toString(),
            MAX_LINE_LENGTH_PROPERTY to maxLineLength.toString()
        )
}
