package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_FILES_KEY
import org.jetbrains.kotlin.psi.KtFile

class KtFileCountVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		file.putUserData(NUMBER_OF_FILES_KEY, 1)
	}
}
