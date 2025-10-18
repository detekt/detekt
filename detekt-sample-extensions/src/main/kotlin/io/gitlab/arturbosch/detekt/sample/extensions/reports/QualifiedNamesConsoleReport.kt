package io.gitlab.arturbosch.detekt.sample.extensions.reports

import dev.detekt.api.ConsoleReport
import dev.detekt.api.Detektion

class QualifiedNamesConsoleReport : ConsoleReport {
    override val id = "QualifiedNamesConsoleReport"
    override fun render(detektion: Detektion): String? = qualifiedNamesReport(detektion)
}
