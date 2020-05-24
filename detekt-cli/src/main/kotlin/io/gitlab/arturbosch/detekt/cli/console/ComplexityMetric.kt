package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey

class ComplexityMetric(detektion: Detektion) {

    val mcc = detektion.getData(complexityKey)
    val loc = detektion.getData(linesKey)
    val sloc = detektion.getData(sourceLinesKey)
    val lloc = detektion.getData(logicalLinesKey)
    val cloc = detektion.getData(commentLinesKey)
    val findings = detektion.findings.entries
}
