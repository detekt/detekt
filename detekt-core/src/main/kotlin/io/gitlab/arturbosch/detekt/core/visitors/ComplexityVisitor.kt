package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.Context
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class ComplexityVisitor : ReturningVisitor<Int>() {

	override var value: Int = 0

	override fun visitKtFile(context: Context, file: KtFile) {
		with(McCabeVisitor()) {
			file.accept(this, context)
			value = mcc
		}
	}

	override fun reset() {
		value = 0
	}
}