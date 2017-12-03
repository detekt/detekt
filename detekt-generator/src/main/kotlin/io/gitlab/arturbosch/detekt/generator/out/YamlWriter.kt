package io.gitlab.arturbosch.detekt.generator.out

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Marvin Ramin
 */
object YamlWriter {
	private val ending: String = "yml"

	fun write(path: Path, fileName: String, content: String) {
		val filePath = path.resolve("$fileName.$ending")
		filePath.parent?.let { Files.createDirectories(it) }
		Files.write(filePath, content.toByteArray())
		println("Wrote: $fileName.$ending")
	}
}

fun yamlFile(path: Path, fileName: String, content: () -> String) {
	YamlWriter.write(path, fileName, content())
}
