package io.gitlab.arturbosch.detekt.sample.extensions.reports

import dev.detekt.api.Detektion
import dev.detekt.api.OutputReport

class QualifiedNamesOutputReport : OutputReport() {

    override val id: String = "QualifiedNamesOutputReport"
    override val ending: String = "txt"

    override fun render(detektion: Detektion): String? = qualifiedNamesReport(detektion)
}
