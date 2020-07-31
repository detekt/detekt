package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.metrics.ComplexityReportGenerator
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

/**
 * Contains metrics concerning the analyzed code.
 * For instance the source lines of code and the McCabe complexity are calculated.
 * See: https://detekt.github.io/detekt/configurations.html#console-reports
 */
class ComplexityReport : ConsoleReport() {

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
