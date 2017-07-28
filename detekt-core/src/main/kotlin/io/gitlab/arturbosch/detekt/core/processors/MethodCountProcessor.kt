package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.processors.util.collectByType
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodCountProcessor : AbstractProjectMetricProcessor() {

	override val visitor = MethodCountVisitor()
	override val key = NUMBER_OF_METHODS_KEY
}

val NUMBER_OF_METHODS_KEY = Key<Int>("number of methods")

class MethodCountVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.putUserData(NUMBER_OF_METHODS_KEY, file.collectByType<KtNamedFunction>().size)
	}
}
