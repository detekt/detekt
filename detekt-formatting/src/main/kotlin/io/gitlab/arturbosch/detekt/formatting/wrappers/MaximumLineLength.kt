package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.MaxLineLengthRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.formatting.ANDROID_MAX_LINE_LENGTH
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_IDEA_LINE_LENGTH
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.MAX_LINE_LENGTH_KEY

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @configuration maxLineLength - maximum line length (default: `120`)
 * @configuration ignoreBackTickedIdentifier - ignore back ticked identifier (default: `false`)
 */
@ActiveByDefault(since = "1.0.0")
@OptIn(FeatureInAlphaState::class)
class MaximumLineLength(config: Config) : FormattingRule(config) {

    override val wrapping = MaxLineLengthRule()
    override val issue = issueFor("Reports lines with exceeded length")

    override val defaultRuleIdAliases: Set<String>
        get() = setOf("MaxLineLength")

    private val defaultMaxLineLength =
        if (isAndroid) ANDROID_MAX_LINE_LENGTH
        else DEFAULT_IDEA_LINE_LENGTH

    private val maxLineLength: Int = valueOrDefault(MAX_LINE_LENGTH, defaultMaxLineLength)
    private val ignoreBackTickedIdentifier = valueOrDefault(IGNORE_BACK_TICKED_IDENTIFIER, false)

    override fun overrideEditorConfig() = mapOf(MAX_LINE_LENGTH_KEY to maxLineLength)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(MaxLineLengthRule.ignoreBackTickedIdentifierProperty to ignoreBackTickedIdentifier.toString())

    companion object {
        const val MAX_LINE_LENGTH = "maxLineLength"
        const val IGNORE_BACK_TICKED_IDENTIFIER = "ignoreBackTickedIdentifier"
    }
}
