package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Dept
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile

class MaxLineLength(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "", Dept.FIVE_MINS)

	private val maxLineLength: Int = valueOrDefault(MAX_LINE_LENGTH, DEFAULT_IDEA_LINE_LENGTH)

	override fun visitKtFile(file: KtFile) {
		var offset = 0
		file.text.splitToSequence("\n")
				.map { it.length }
				.forEach {
					offset += it
					if (it > maxLineLength) {
						report(CodeSmell(issue, Entity.from(file, offset)))
					}
				}
	}

	companion object {
		val MAX_LINE_LENGTH = "maxLineLength"
		val DEFAULT_IDEA_LINE_LENGTH = 120
	}
}

