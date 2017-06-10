package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtFile
import java.io.BufferedReader
import java.io.InputStreamReader

class MaxLineLength(config: Config = Config.empty) : Rule("MaxLineLength", Severity.Style, config) {

	private val maxLineLength: Int = withConfig { valueOrDefault(MAX_LINE_LENGTH) { "100" } }.toInt()

	override fun visit(root: KtFile) {
		val bufferedReader = BufferedReader(InputStreamReader(root.text.byteInputStream()))

		var offset = 0
		bufferedReader.lines()
				.map { it.length }
				.forEach {
					offset += it
					if (it > maxLineLength) {
						addFindings(CodeSmell(id, Entity.Companion.from(root, offset)))
					}
				}
	}

	companion object {
		val MAX_LINE_LENGTH = "maxLineLength"
	}
}

