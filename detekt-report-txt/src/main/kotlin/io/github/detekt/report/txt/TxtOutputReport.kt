package io.github.detekt.report.txt

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

class TxtOutputReport : OutputReport() {

    override val ending: String = "txt"

    override val name = "plain text report"

    override fun render(detektion: Detektion): String {
        val builder = StringBuilder()
        detektion.findings
            .flatMap { it.value }
            .forEach { builder.append(it.compactWithSignature()).append("\n") }
        return builder.toString()
    }
}
