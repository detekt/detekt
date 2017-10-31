package io.gitlab.arturbosch.detekt.generator.out

import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Marvin Ramin
 */
class YamlWriter {
	private val ending: String = "yml"

	fun write(fileName: String, content: String) {
		val filePath = Paths.get("./detekt-generator/documentation/").resolve("$fileName.$ending")
		filePath.parent?.let { Files.createDirectories(it) }
		Files.write(filePath, content.toByteArray())
		println("Wrote: $fileName.$ending")
	}
}
