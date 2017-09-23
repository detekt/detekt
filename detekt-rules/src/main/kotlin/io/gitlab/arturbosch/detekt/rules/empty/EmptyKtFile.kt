package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.psi.KtFile

class EmptyKtFile(config: Config) : EmptyRule(config) {

	override fun visitKtFile(file: KtFile) {
		if (file.text.isNullOrBlank()) {
			report(CodeSmell(issue, Entity.from(file)))
		}
	}
}
