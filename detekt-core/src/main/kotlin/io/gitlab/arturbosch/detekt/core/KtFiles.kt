package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.fileClasses.javaFileFacadeFqName
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */

fun List<KtFile>.saveModifiedFiles(project: Path, notification: (Notification) -> Unit) {
	this.filter { it.modificationStamp > 0 }
			.map { it.relativePath to it.unnormalizedContent() }
			.filter { it.first != null }
			.map { project.resolve(it.first) to it.second }
			.forEach {
				notification.invoke(ModificationNotification(it.first))
				Files.write(it.first, it.second.toByteArray())
			}
}

fun KtFile.unnormalizedContent(): String {
	val lineSeparator = this.getUserData(KtCompiler.LINE_SEPARATOR)
	require(lineSeparator != null) { "No line separator entry for ktFile ${this.javaFileFacadeFqName.asString()}" }
	return this.text.replace("\n", lineSeparator!!)
}

val KtFile.relativePath: String?
	get() = this.getUserData(KtCompiler.RELATIVE_PATH)
