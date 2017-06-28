package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.fileClasses.javaFileFacadeFqName
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */

fun KtFile.unnormalizedContent(): String {
	val lineSeparator = this.getUserData(KtCompiler.LINE_SEPARATOR)
	require(lineSeparator != null) { "No line separator entry for ktFile ${this.javaFileFacadeFqName.asString()}" }
	return StringUtilRt.convertLineSeparators(text, lineSeparator!!)
}

val KtFile.relativePath: String?
	get() = this.getUserData(KtCompiler.RELATIVE_PATH)
