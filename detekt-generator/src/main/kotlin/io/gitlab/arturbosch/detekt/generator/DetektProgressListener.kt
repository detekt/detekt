package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Marvin Ramin
 */
class DetektProgressListener : FileProcessListener {

	override fun onStart(files: List<KtFile>) {
		val name = if (files.size == 1) "file" else "files"
		print("Analyzing ${files.size} kotlin $name: ")
	}

	override fun onProcess(file: KtFile) {
		print(".")
	}
}
