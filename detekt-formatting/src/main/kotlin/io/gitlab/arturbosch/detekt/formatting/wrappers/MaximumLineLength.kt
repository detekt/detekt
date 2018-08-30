package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.core.EditorConfig
import com.github.shyiko.ktlint.ruleset.standard.MaxLineLengthRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.ANDROID_MAX_LINE_LENGTH
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_IDEA_LINE_LENGTH
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.merge

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @configuration maxLineLength - maximum line length (default: 120)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class MaximumLineLength(config: Config) : FormattingRule(config) {

	override val wrapping = MaxLineLengthRule()
	override val issue = issueFor("Reports lines with exceeded length")

	private val defaultMaxLineLength =
			if (isAndroid) ANDROID_MAX_LINE_LENGTH
			else DEFAULT_IDEA_LINE_LENGTH
	private val maxLineLength: Int = valueOrDefault(MAX_LINE_LENGTH, defaultMaxLineLength)

	override fun editorConfigUpdater(): ((oldEditorConfig: EditorConfig?) -> EditorConfig)? = {
		EditorConfig.merge(it,
			maxLineLength = maxLineLength)
	}
}

private const val MAX_LINE_LENGTH = "maxLineLength"
