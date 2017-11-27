package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.processors.util.collectByType
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile

class ClassCountProcessor : AbstractProjectMetricProcessor() {

	override val visitor = ClassCountVisitor()
	override val key = numberOfClassesKey
}

val numberOfClassesKey = Key<Int>("number of classes")

class ClassCountVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.putUserData(numberOfClassesKey, file.collectByType<KtClass>().size)
	}
}
