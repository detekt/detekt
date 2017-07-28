package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.processors.util.collectByType
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty

class FieldCountProcessor : AbstractProjectMetricProcessor() {

	override val visitor = FieldCountVisitor()
	override val key = NUMBER_OF_FIELDS_KEY
}

val NUMBER_OF_FIELDS_KEY = Key<Int>("number of fields")

class FieldCountVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.putUserData(NUMBER_OF_FIELDS_KEY, file.collectByType<KtProperty>().size)
	}
}
