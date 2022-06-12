package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties
import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.MaxLineLengthRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io) for documentation.
 *
 * This rules overlaps with [style>MaxLineLength](https://detekt.dev/style.html#maxlinelength)
 * from the standard rules, make sure to enable just one or keep them aligned. The pro of this rule is that it can
 * auto-correct the issue.
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

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            MaxLineLengthRule.ignoreBackTickedIdentifierProperty to ignoreBackTickedIdentifier.toString(),
            DefaultEditorConfigProperties.maxLineLengthProperty to maxLineLength.toString(),
        )
}
