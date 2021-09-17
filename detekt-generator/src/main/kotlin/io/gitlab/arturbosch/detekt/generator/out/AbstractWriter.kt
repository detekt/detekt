package io.gitlab.arturbosch.detekt.generator.out

import java.nio.file.Files
import java.nio.file.Path

internal abstract class AbstractWriter {

    protected abstract val ending: String

    fun write(path: Path, fileName: String, content: () -> String) {
        val filePath = path.resolve("$fileName.$ending")
        filePath.parent?.let { parentPath ->
            if (!Files.exists(parentPath)) {
                Files.createDirectories(parentPath)
            }
        }
        Files.write(filePath, content().toByteArray())
        println("Wrote: $filePath")
    }
}

internal class MarkdownWriter : AbstractWriter() {

    override val ending = "md"
}

internal class YamlWriter : AbstractWriter() {

    override val ending = "yml"
}

internal class PropertiesWriter : AbstractWriter() {

    override val ending = "properties"
}
