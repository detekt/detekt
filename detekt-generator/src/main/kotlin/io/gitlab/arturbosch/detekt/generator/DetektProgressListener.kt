package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Marvin Ramin
 */
class DetektProgressListener : FileProcessListener {

	override fun onStart(files: List<KtFile>) {
		print("Analyzing ${files.size} kotlin files: ")
	}

	override fun onProcess(file: KtFile) {
		print(".")
	}
}
