package io.gitlab.arturbosch.detekt.generator.printer

import com.beust.jcommander.JCommander
import io.gitlab.arturbosch.detekt.cli.CliArgs
import java.nio.file.Path
import kotlin.io.path.writeText

class CliOptionsPrinter {

    private val jCommander = JCommander(CliArgs()).apply {
        programName = "detekt"
    }

    fun print(filePath: Path) {
        filePath.writeText(
            buildString {
                appendLine("```")
                jCommander.usageFormatter.usage(this)
                appendLine("```")
            }
        )
    }
}
