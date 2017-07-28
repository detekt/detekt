package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.processors.util.collectByType
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class FunctionCountProcessor : AbstractProjectMetricProcessor() {

	override val visitor = FunctionCountVisitor()
	override val key = NUMBER_OF_FUNCTIONS_KEY
}

val NUMBER_OF_FUNCTIONS_KEY = Key<Int>("number of functions")

class FunctionCountVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.putUserData(NUMBER_OF_FUNCTIONS_KEY, file.collectByType<KtNamedFunction>().size)
	}
}
