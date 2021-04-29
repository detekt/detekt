package io.gitlab.arturbosch.detekt.generator.printer

import com.beust.jcommander.JCommander
import io.gitlab.arturbosch.detekt.cli.CliArgs
import java.nio.file.Files
import java.nio.file.Path

class CliOptionsPrinter {

    private val jCommander = JCommander(CliArgs()).apply {
        programName = "detekt"
    }

    fun print(filePath: Path) {
        Files.write(
            filePath,
            buildString {
                appendLine("```")
                jCommander.usageFormatter.usage(this)
                appendLine("```")
            }.toByteArray()
        )
    }
}
