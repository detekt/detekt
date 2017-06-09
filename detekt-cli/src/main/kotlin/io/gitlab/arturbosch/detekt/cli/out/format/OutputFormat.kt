package io.gitlab.arturbosch.detekt.cli.out.format

import io.gitlab.arturbosch.detekt.api.Finding
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
abstract class OutputFormat(val report: Path) {

    fun create(smells: List<Finding>) {
        val smellData = render(smells)
        smellData?.let {
            report.parent?.let { Files.createDirectories(it) }
            Files.write(report, it.toByteArray())
            println("Successfully wrote findings to $report")
        }
    }

    abstract fun render(smells: List<Finding>): String?

    enum class Formatter {
        PLAIN,
        XML;

        fun create(report: Path): OutputFormat = when (this) {
            PLAIN -> PlainOutputFormat(report)
            XML -> XmlOutputFormat(report)
        }
    }
}
