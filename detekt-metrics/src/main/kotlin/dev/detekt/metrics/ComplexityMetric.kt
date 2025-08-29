package dev.detekt.metrics

import dev.detekt.api.Detektion
import dev.detekt.api.suppressed
import dev.detekt.metrics.processors.commentLinesKey
import dev.detekt.metrics.processors.complexityKey
import dev.detekt.metrics.processors.linesKey
import dev.detekt.metrics.processors.logicalLinesKey
import dev.detekt.metrics.processors.sourceLinesKey

class ComplexityMetric(detektion: Detektion) {

    val mcc = detektion.userData[complexityKey.toString()] as Int?
    val cognitiveComplexity = detektion.userData[CognitiveComplexity.KEY.toString()] as Int?
    val loc = detektion.userData[linesKey.toString()] as Int?
    val sloc = detektion.userData[sourceLinesKey.toString()] as Int?
    val lloc = detektion.userData[logicalLinesKey.toString()] as Int?
    val cloc = detektion.userData[commentLinesKey.toString()] as Int?
    val issuesCount = detektion.issues.count { !it.suppressed }
}
