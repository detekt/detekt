package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.core.EditorConfig
import com.github.shyiko.ktlint.core.KtLint
import com.github.shyiko.ktlint.ruleset.standard.MaxLineLengthRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.ANDROID_MAX_LINE_LENGTH
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_IDEA_LINE_LENGTH
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import org.jetbrains.kotlin.psi.KtFile

/**
 * See https://ktlint.github.io for documentation.
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

	override fun visit(root: KtFile) {
		super.visit(root)
		root.node.putUserData(KtLint.EDITOR_CONFIG_USER_DATA_KEY,
				EditorConfig.fromMap(mapOf(MAX_LINE_LENGTH to maxLineLength.toString())))
	}
}

private const val MAX_LINE_LENGTH = "maxLineLength"
