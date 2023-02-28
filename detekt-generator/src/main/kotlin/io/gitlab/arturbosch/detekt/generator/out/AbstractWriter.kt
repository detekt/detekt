package io.gitlab.arturbosch.detekt.generator.out

import java.io.PrintStream
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.writeText

internal abstract class AbstractWriter(
    private val outputPrinter: PrintStream,
) {

    protected abstract val ending: String

    fun write(path: Path, fileName: String, content: () -> String) {
        val filePath = path.resolve("$fileName.$ending")
        filePath.parent?.let { parentPath ->
            if (!parentPath.exists()) {
                parentPath.createDirectories()
            }
        }
        filePath.writeText(content())
        outputPrinter.println("Wrote: $filePath")
    }
}

internal class MarkdownWriter(outputPrinter: PrintStream) : AbstractWriter(outputPrinter) {

    override val ending = "md"
}

internal class YamlWriter(outputPrinter: PrintStream) : AbstractWriter(outputPrinter) {

    override val ending = "yml"
}

internal class PropertiesWriter(outputPrinter: PrintStream) : AbstractWriter(outputPrinter) {

    override val ending = "properties"
}
