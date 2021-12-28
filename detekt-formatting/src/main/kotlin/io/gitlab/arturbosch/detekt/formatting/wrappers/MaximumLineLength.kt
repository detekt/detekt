package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.MaxLineLengthRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.MAX_LINE_LENGTH_KEY

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * This rules overlaps with [`MaxLineLength`](https://detekt.github.io/detekt/style.html#maxlinelength) from the standard rules, make sure to enable just one or keep them aligned.
 */
@ActiveByDefault(since = "1.0.0")
@OptIn(FeatureInAlphaState::class)
class MaximumLineLength(config: Config) : FormattingRule(config) {

    override val wrapping = MaxLineLengthRule()
    override val issue = issueFor("Reports lines with exceeded length")

    override val defaultRuleIdAliases: Set<String>
        get() = setOf("MaxLineLength")

    @Configuration("maximum line length")
    private val maxLineLength: Int by configWithAndroidVariants(120, 100)

    @Configuration("ignore back ticked identifier")
    private val ignoreBackTickedIdentifier by config(false)

    override fun overrideEditorConfig() = mapOf(MAX_LINE_LENGTH_KEY to maxLineLength)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(MaxLineLengthRule.ignoreBackTickedIdentifierProperty to ignoreBackTickedIdentifier.toString())
}
