package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.LLOC_KEY
import io.gitlab.arturbosch.detekt.core.visitors.util.LLOC
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class LLOCVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		val lines = file.text.split("\n")
		val value = LLOC.analyze(lines)
		file.putUserData(LLOC_KEY, value)
	}

}
