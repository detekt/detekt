package io.gitlab.arturbosch.detekt.generator.out

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Marvin Ramin
 */
object MarkdownWriter {
	private val ending: String = "md"

	fun write(path: Path, fileName: String, content: String) {
		val filePath = path.resolve("$fileName.$ending")
		filePath.parent?.let { Files.createDirectories(it) }
		Files.write(filePath, content.toByteArray())
		println("Wrote: $fileName.$ending")
	}
}

fun markdownFile(path: Path, fileName: String, content: () -> String) {
	MarkdownWriter.write(path, fileName, content())
}
