package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_METHODS_KEY
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodCountVisitor : DetektVisitor() {

	private var count = 0

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.putUserData(NUMBER_OF_METHODS_KEY, count)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		count++
		super.visitNamedFunction(function)
	}
}
