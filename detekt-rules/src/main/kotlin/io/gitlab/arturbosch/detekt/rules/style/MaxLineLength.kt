package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile

class MaxLineLength(config: Config = Config.empty) : Rule("MaxLineLength", config) {

	private val maxLineLength: Int = withConfig { valueOrDefault(MAX_LINE_LENGTH, 120) }

	override fun visitKtFile(context: Context, file: KtFile) {
		var offset = 0
		file.text.splitToSequence("\n")
				.map { it.length }
				.forEach {
					offset += it
					if (it > maxLineLength) {
						context.report(CodeSmell(ISSUE, Entity.Companion.from(file.findElementAt(offset) ?: file)))
					}
				}
	}

	companion object {
		val MAX_LINE_LENGTH = "maxLineLength"
		val ISSUE = Issue("MaxLineLength", Issue.Severity.Style)
	}
}

