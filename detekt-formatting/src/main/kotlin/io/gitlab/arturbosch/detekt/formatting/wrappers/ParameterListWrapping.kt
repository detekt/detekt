package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.core.EditorConfig
import com.github.shyiko.ktlint.core.KtLint
import com.github.shyiko.ktlint.ruleset.standard.ParameterListWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.DEFAULT_INDENT
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import org.jetbrains.kotlin.psi.KtFile

/**
 * See https://ktlint.github.io for documentation.
 *
 * @configuration indentSize - indentation size (default: 4)
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class ParameterListWrapping(config: Config) : FormattingRule(config) {

	override val wrapping = ParameterListWrappingRule()
	override val issue = issueFor("Detects mis-aligned parameter lists")

	private val indentSize = valueOrDefault(INDENT_SIZE, DEFAULT_INDENT)

	override fun visit(root: KtFile) {
		super.visit(root)
		root.node.putUserData(KtLint.EDITOR_CONFIG_USER_DATA_KEY,
				EditorConfig.fromMap(mapOf(INDENT_SIZE to indentSize.toString())))
	}
}

private const val INDENT_SIZE = "indentSize"
