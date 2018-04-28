package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.core.EditorConfig
import com.github.shyiko.ktlint.core.KtLint
import com.github.shyiko.ktlint.ruleset.standard.IndentationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_CONTINUATION_INDENT
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_INDENT
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import org.jetbrains.kotlin.psi.KtFile

/**
 * See https://ktlint.github.io/#rule-indentation for documentation.
 *
 * @configuration indentSize - indentation size (default: 4)
 * @configuration continuationIndentSize - continuation indentation size (default: 4)
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class Indentation(config: Config) : FormattingRule(config) {

	override val wrapping = IndentationRule()
	override val issue = issueFor("Reports mis-indented code")

	private val indentSize = valueOrDefault(INDENT_SIZE, DEFAULT_INDENT)
	private val continuationIndentSize = valueOrDefault(CONTINUATION_INDENT_SIZE, DEFAULT_CONTINUATION_INDENT)

	override fun visit(root: KtFile) {
		super.visit(root)
		root.node.putUserData(KtLint.EDITOR_CONFIG_USER_DATA_KEY,
				EditorConfig.fromMap(mapOf(
						INDENT_SIZE to indentSize.toString(),
						CONTINUATION_INDENT_SIZE to continuationIndentSize.toString())))
	}
}

private const val INDENT_SIZE = "indentSize"
private const val CONTINUATION_INDENT_SIZE = "continuationIndentSize"
