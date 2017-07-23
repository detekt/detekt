package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.com.intellij.openapi.util.Key

/**
 * @author Artur Bosch
 */
@Suppress("EmptyFunctionBlock")
interface FileProcessListener {
	fun onStart(files: List<KtFile>) {}
	fun onProcess(file: KtFile) {}
	fun onFinish(files: List<KtFile>, result: Detektion) {}
}

val COMPLEXITY_KEY = Key<Int>("complexity")
val LLOC_KEY = Key<Int>("lloc")
val NUMBER_OF_CLASSES_KEY = Key<Int>("number of classes")
val NUMBER_OF_METHODS_KEY = Key<Int>("number of methods")
val NUMBER_OF_FIELDS_KEY = Key<Int>("number of fields")
