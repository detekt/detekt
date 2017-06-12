package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.core.visitors.util.LLOC
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class LLOCVisitor : ReturningVisitor<Int>() {

	override var value: Int = 0

	override fun visitKtFile(context: Context, file: KtFile) {
		val lines = file.text.split("\n")
		value = LLOC.analyze(lines)
	}

	override fun reset() {
		value = 0
	}

}
