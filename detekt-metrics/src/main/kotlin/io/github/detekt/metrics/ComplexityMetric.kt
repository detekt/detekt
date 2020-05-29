package io.github.detekt.metrics

import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.gitlab.arturbosch.detekt.api.Detektion

class ComplexityMetric(detektion: Detektion) {

    val mcc = detektion.getData(complexityKey)
    val cognitiveComplexity = detektion.getData(CognitiveComplexity.KEY)
    val loc = detektion.getData(linesKey)
    val sloc = detektion.getData(sourceLinesKey)
    val lloc = detektion.getData(logicalLinesKey)
    val cloc = detektion.getData(commentLinesKey)
    val findings = detektion.findings.entries
}
