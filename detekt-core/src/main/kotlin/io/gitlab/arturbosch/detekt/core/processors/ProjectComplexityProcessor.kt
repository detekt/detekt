package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.internal.McCabeVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class ProjectComplexityProcessor : AbstractProcessor() {

	override val visitor = ComplexityVisitor()
	override val key = COMPLEXITY_KEY
}

val COMPLEXITY_KEY = Key<Int>("complexity")

class ComplexityVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		with(McCabeVisitor()) {
			file.accept(this)
			file.putUserData(COMPLEXITY_KEY, mcc)
		}
	}
}
