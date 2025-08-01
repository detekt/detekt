package io.github.detekt.metrics

import dev.detekt.api.Detektion
import dev.detekt.api.suppressed
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey

class ComplexityMetric(detektion: Detektion) {

    val mcc = detektion.getUserData(complexityKey)
    val cognitiveComplexity = detektion.getUserData(CognitiveComplexity.KEY)
    val loc = detektion.getUserData(linesKey)
    val sloc = detektion.getUserData(sourceLinesKey)
    val lloc = detektion.getUserData(logicalLinesKey)
    val cloc = detektion.getUserData(commentLinesKey)
    val issuesCount = detektion.issues.count { !it.suppressed }
}
