package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.core.visitors.ComplexityVisitor
import io.gitlab.arturbosch.detekt.core.visitors.LLOCVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
@Suppress("EmptyFunctionBlock")
interface FileProcessListener {
	fun onStart(files: List<KtFile>) {}
	fun onProcess(file: KtFile) {}
	fun onFinish(files: List<KtFile>, result: Detektion) {}
}

class ProjectComplexityProcessor : FileProcessListener {

	private val complexityVisitor = ComplexityVisitor()

	override fun onProcess(file: KtFile) {
		val value = complexityVisitor.visitAndReturn(file)
		file.putUserData(COMPLEXITY_KEY, value)
	}

	override fun onFinish(files: List<KtFile>, result: Detektion) {
		val complexity = files
				.map { it.getUserData(COMPLEXITY_KEY) }
				.filterNotNull()
				.sum()
		result.addData(COMPLEXITY_KEY, complexity)
	}

}

class ProjectLLOCProcessor : FileProcessListener {

	private val llocVisitor = LLOCVisitor()

	override fun onProcess(file: KtFile) {
		val value = llocVisitor.visitAndReturn(file)
		file.putUserData(LLOC_KEY, value)
	}

	override fun onFinish(files: List<KtFile>, result: Detektion) {
		val lloc = files
				.map { it.getUserData(LLOC_KEY) }
				.filterNotNull()
				.sum()
		result.addData(LLOC_KEY, lloc)
	}

}

val COMPLEXITY_KEY = Key<Int>("complexity")
val LLOC_KEY = Key<Int>("lloc")