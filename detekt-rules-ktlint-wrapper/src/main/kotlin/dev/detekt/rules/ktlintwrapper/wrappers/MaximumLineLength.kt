package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.MAX_LINE_LENGTH_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.MaxLineLengthRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import dev.detekt.rules.ktlintwrapper.configWithAndroidVariants

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#max-line-length) for documentation.
 *
 * This rules overlaps with [style>MaxLineLength](https://detekt.dev/style.html#maxlinelength)
 * from the standard rules, make sure to enable just one or keep them aligned.
 */
@ActiveByDefault(since = "1.0.0")
@Alias("MaxLineLength")
class MaximumLineLength(config: Config) : KtlintRule(
    config,
    "Reports lines with exceeded length"
) {

    override val wrapping = MaxLineLengthRule()

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    @Configuration("ignore back ticked identifier")
    private val ignoreBackTickedIdentifier by config(false)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            MaxLineLengthRule.IGNORE_BACKTICKED_IDENTIFIER_PROPERTY to ignoreBackTickedIdentifier.toString(),
            MAX_LINE_LENGTH_PROPERTY to maxLineLength.toString(),
        )
}
