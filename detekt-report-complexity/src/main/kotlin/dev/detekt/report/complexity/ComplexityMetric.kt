package dev.detekt.report.complexity

import dev.detekt.api.Detektion
import dev.detekt.api.suppressed

class ComplexityMetric(detektion: Detektion) {

    val mcc = detektion.userData[cyclomaticComplexityKey.toString()] as Int?
    val cognitiveComplexity = detektion.userData[cognitiveComplexityKey.toString()] as Int?
    val loc = detektion.userData[linesKey.toString()] as Int?
    val sloc = detektion.userData[sourceLinesKey.toString()] as Int?
    val lloc = detektion.userData[logicalLinesKey.toString()] as Int?
    val cloc = detektion.userData[commentLinesKey.toString()] as Int?
    val issuesCount = detektion.issues.count { !it.suppressed }
}
