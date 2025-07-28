package dev.detekt.metrics

import dev.detekt.metrics.processors.commentLinesKey
import dev.detekt.metrics.processors.complexityKey
import dev.detekt.metrics.processors.linesKey
import dev.detekt.metrics.processors.logicalLinesKey
import dev.detekt.metrics.processors.sourceLinesKey
import dev.detekt.api.Detektion
import dev.detekt.api.suppressed

class ComplexityMetric(detektion: Detektion) {

    val mcc = detektion.getUserData(complexityKey)
    val cognitiveComplexity = detektion.getUserData(CognitiveComplexity.KEY)
    val loc = detektion.getUserData(linesKey)
    val sloc = detektion.getUserData(sourceLinesKey)
    val lloc = detektion.getUserData(logicalLinesKey)
    val cloc = detektion.getUserData(commentLinesKey)
    val issuesCount = detektion.issues.count { !it.suppressed }
}
