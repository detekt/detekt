package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.Detektion
import io.gitlab.arturbosch.detekt.core.FileProcessListener
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

abstract class AbstractProcessor : FileProcessListener {

	protected abstract val visitor: DetektVisitor

	protected abstract val key: Key<Int>

	override fun onProcess(file: KtFile) {
		file.accept(visitor)
	}

	override fun onFinish(files: List<KtFile>, result: Detektion) {
		val count = files
				.map { it.getUserData(key) }
				.filterNotNull()
				.sum()
		result.addData(key, count)
	}
}