package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.FinalNewlineRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @configuration insertFinalNewLine - report absence or presence of a newline (default: `true`)
 */
@OptIn(FeatureInAlphaState::class)
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class FinalNewline(config: Config) : FormattingRule(config) {

    override val wrapping = FinalNewlineRule()
    override val issue = issueFor("Detects missing final newlines")

    private val insertFinalNewline = valueOrDefault(INSERT_FINAL_NEWLINE, true)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(FinalNewlineRule.insertNewLineProperty to insertFinalNewline.toString())
}

const val INSERT_FINAL_NEWLINE = "insertFinalNewLine"
