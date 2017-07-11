package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.COMPLEXITY_KEY
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class ComplexityVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		with(McCabeVisitor()) {
			file.accept(this)
			file.putUserData(COMPLEXITY_KEY, mcc)
		}
	}

}
