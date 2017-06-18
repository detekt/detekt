package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtFile

class MaxLineLength(config: Config = Config.empty) : Rule("MaxLineLength", Severity.Style, config) {

	private val maxLineLength: Int = withConfig { valueOrDefault(MAX_LINE_LENGTH, DEFAULT_IDEA_LINE_LENGTH) }

	override fun visitKtFile(file: KtFile) {
		var offset = 0
		file.text.splitToSequence("\n")
				.map { it.length }
				.forEach {
					offset += it
					if (it > maxLineLength) {
						addFindings(CodeSmell(id, severity, Entity.Companion.from(file, offset)))
					}
				}
	}

	companion object {
		val MAX_LINE_LENGTH = "maxLineLength"
		val DEFAULT_IDEA_LINE_LENGTH = 120
	}
}

