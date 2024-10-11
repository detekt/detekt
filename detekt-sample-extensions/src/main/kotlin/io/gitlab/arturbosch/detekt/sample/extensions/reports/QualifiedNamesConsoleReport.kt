package io.gitlab.arturbosch.detekt.sample.extensions.reports

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class QualifiedNamesConsoleReport : ConsoleReport() {

    override fun render(detektion: Detektion): String? {
        return qualifiedNamesReport(detektion)
    }
}
