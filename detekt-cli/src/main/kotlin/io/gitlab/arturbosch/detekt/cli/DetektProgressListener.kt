package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class DetektProgressListener : FileProcessListener {

	override fun onProcess(file: KtFile) {
		kotlin.io.print(".")
	}

	override fun onFinish(files: List<KtFile>, result: Detektion) {
		val middlePart = if (files.size == 1) "file was" else "files were"
		kotlin.io.println("\n\n${files.size} kotlin $middlePart analyzed.")
	}
}
