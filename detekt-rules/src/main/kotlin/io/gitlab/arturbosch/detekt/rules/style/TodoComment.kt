package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Dept
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile

class TodoComment(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Detects todo comments.",
			Dept.FIVE_MINS)

	override fun visitKtFile(file: KtFile) {
		var offset = 0

		file.text.splitToSequence("\n")
				.forEach {
					offset += it.length
					if (it.containsIgnoreCase("// TODO:") || it.containsIgnoreCase("//TODO:")) {
						report(CodeSmell(issue, Entity.from(file, offset)))
					}
				}
	}

	private fun String.containsIgnoreCase(other: String) = this.contains(other, true)

}

