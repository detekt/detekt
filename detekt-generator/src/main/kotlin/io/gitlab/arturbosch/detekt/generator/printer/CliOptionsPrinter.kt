package io.gitlab.arturbosch.detekt.generator.printer

import com.beust.jcommander.JCommander
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.generator.GeneratorArgs
import io.gitlab.arturbosch.detekt.generator.out.MarkdownWriter

class CliOptionsPrinter(private val arguments: GeneratorArgs) {

    private val jCommander = JCommander(CliArgs()).apply {
        programName = "detekt"
    }

    private val header = """
        ---
        title: Command Line Interface Options
        sidebar: home_sidebar
        keywords: cli
        permalink: cli-options.html
        folder: gettingstarted
        summary:
        ---
    """.trimIndent()

    fun print() {
        MarkdownWriter().write(arguments.cliOptionsPath, "cli-options") {
            buildString {
                append(header)
                appendLine()
                appendLine("```")
                jCommander.usageFormatter.usage(this)
                appendLine("```")
            }
        }
    }
}
