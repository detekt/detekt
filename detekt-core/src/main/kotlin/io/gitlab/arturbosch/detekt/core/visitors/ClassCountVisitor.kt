package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_CLASSES_KEY
import io.gitlab.arturbosch.detekt.core.visitors.util.collectByType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile

class ClassCountVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.putUserData(NUMBER_OF_CLASSES_KEY, file.collectByType<KtClass>().size)
	}
}
