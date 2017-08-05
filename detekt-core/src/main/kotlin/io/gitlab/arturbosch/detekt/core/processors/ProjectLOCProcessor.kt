package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class ProjectLOCProcessor : AbstractProcessor() {

	override val visitor: DetektVisitor = LOCVisitor()
	override val key = LOC_KEY
}

class LOCVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		val lines = file.text.count { it == '\n' } + 1
		file.putUserData(LOC_KEY, lines)
	}
}

val LOC_KEY = Key<Int>("loc")
