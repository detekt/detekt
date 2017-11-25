package io.gitlab.arturbosch.detekt.generator.out

import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Marvin Ramin
 */
object YamlWriter {
	private val ending: String = "yml"

	fun write(fileName: String, content: String) {
		val filePath = Paths.get("./detekt-generator/documentation/").resolve("$fileName.$ending")
		filePath.parent?.let { Files.createDirectories(it) }
		Files.write(filePath, content.toByteArray())
		println("Wrote: $fileName.$ending")
	}
}

fun yamlFile(fileName: String, content: () -> String) {
	YamlWriter.write(fileName, content())
}
