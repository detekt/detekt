package io.github.detekt.metrics

import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.suppressed

class ComplexityMetric(detektion: Detektion) {

    val mcc = detektion.getUserData(complexityKey)
    val cognitiveComplexity = detektion.getUserData(CognitiveComplexity.KEY)
    val loc = detektion.getUserData(linesKey)
    val sloc = detektion.getUserData(sourceLinesKey)
    val lloc = detektion.getUserData(logicalLinesKey)
    val cloc = detektion.getUserData(commentLinesKey)
    val issuesCount = detektion.issues.count { !it.suppressed }
}
