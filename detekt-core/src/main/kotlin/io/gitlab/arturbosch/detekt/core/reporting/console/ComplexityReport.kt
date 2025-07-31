package io.gitlab.arturbosch.detekt.core.reporting.console

import dev.detekt.api.ConsoleReport
import dev.detekt.api.Detektion
import io.github.detekt.metrics.ComplexityReportGenerator

/**
 * Contains metrics concerning the analyzed code.
 * For instance the source lines of code and the McCabe complexity are calculated.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class ComplexityReport : ConsoleReport {

    override val id: String = "ComplexityReport"
    override val priority: Int = 20

    override fun render(detektion: Detektion): String? {
        val complexityReportGenerator = ComplexityReportGenerator.create(detektion)
        return complexityReportGenerator.generate()?.let { list ->
            with(StringBuilder()) {
                append("Complexity Report:\n")
                list.forEach {
                    append("\t- ")
                    append(it)
                    append("\n")
                }
                toString()
            }
        }
    }
}
