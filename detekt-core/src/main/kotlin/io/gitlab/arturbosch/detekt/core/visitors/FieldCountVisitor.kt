package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_FIELDS_KEY
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile

class FieldCountVisitor : DetektVisitor() {

	var count = 0

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.putUserData(NUMBER_OF_FIELDS_KEY, count)
	}

	override fun visitClass(klass: KtClass) {
		count += klass.getProperties().count()
	}
}
