package io.gitlab.arturbosch.detekt.api

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