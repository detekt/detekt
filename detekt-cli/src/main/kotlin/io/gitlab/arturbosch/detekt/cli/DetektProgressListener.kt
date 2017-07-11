package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.core.Detektion
import io.gitlab.arturbosch.detekt.core.FileProcessListener
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class DetektProgressListener : FileProcessListener {
	override fun onStart(files: List<KtFile>) {
		kotlin.io.print("Analyzing ${files.size} kotlin files: ")
	}

	override fun onProcess(file: KtFile) {
		kotlin.io.print(".")
	}

	override fun onFinish(files: List<KtFile>, result: Detektion) {
		kotlin.io.println()
	}
}
