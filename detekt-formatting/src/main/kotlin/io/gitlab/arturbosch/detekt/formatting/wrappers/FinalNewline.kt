package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.FinalNewlineRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * This rules overlaps with [`style>NewLineAtEndOfFile`](https://detekt.github.io/detekt/style.html#newlineatendoffile)
 * from the standard rules, make sure to enable just one. The pro of this rule is that it can auto-correct the issue.
 */
@OptIn(FeatureInAlphaState::class)
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class FinalNewline(config: Config) : FormattingRule(config) {

    override val wrapping = FinalNewlineRule()
    override val issue = issueFor("Detects missing final newlines")

    @Configuration("report absence or presence of a newline")
    private val insertFinalNewLine by config(true)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(FinalNewlineRule.insertNewLineProperty to insertFinalNewLine.toString())
}
