package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.MAX_LINE_LENGTH_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.FunctionReturnTypeSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#function-return-type-spacing) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
@AutoCorrectable(since = "1.22.0")
class FunctionReturnTypeSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionReturnTypeSpacingRule()
    override val issue = issueFor("Checks the spacing between colon and return type.")

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            MAX_LINE_LENGTH_PROPERTY to maxLineLength.toString(),
        )
}
