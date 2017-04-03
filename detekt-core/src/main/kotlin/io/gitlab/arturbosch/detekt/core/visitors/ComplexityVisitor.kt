package io.gitlab.arturbosch.detekt.core.visitors

import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class ComplexityVisitor : ReturningVisitor<Int>() {

	override var value: Int = 0

	override fun visitKtFile(file: KtFile) {
		with(McCabeVisitor()) {
			file.accept(this)
			value = mcc
		}
	}

	override fun reset() {
		value = 0
	}
}