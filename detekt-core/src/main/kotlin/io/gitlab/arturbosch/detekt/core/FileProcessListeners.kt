package io.gitlab.arturbosch.detekt.core

import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
interface FileProcessListener {
	fun onStart(files: List<KtFile>) {}
	fun onProcess(file: KtFile) {}
	fun onFinish(files: List<KtFile>, result: Detektion) {}
}

class ProjectComplexityProcessor : FileProcessListener {

	override fun onProcess(file: KtFile) {
	}

	override fun onFinish(files: List<KtFile>, result: Detektion) {
		val complexity = files
				.map { it.getUserData(COMPLEXITY_KEY) }
				.filterNotNull()
				.sum()
		result.addData(LLOC_KEY, complexity)
	}

}

class ProjectLLOCProcessor : FileProcessListener {

	override fun onProcess(file: KtFile) {
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