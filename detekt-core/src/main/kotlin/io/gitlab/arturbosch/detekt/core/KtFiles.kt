package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.fileClasses.javaFileFacadeFqName
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */

fun KtFile.unnormalizedContent(): String {
	val lineSeparator = this.getUserData(KtCompiler.LINE_SEPARATOR)
	require(lineSeparator != null) { "No line separator entry for ktFile ${this.javaFileFacadeFqName.asString()}" }
	return this.text.replace("\n", lineSeparator!!)
}

val KtFile.relativePath: String?
	get() = this.getUserData(KtCompiler.RELATIVE_PATH)
