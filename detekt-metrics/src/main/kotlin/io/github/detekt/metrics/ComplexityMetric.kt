package io.github.detekt.metrics

import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

data class ComplexityMetric(
    val mcc: Int?,
    val cognitiveComplexity: Int?,
    val loc: Int?,
    val sloc: Int?,
    val lloc: Int?,
    val cloc: Int?,
    val findings: Set<Map.Entry<RuleSetId, List<Finding>>>
) {
    companion object {
        fun create(detektion: Detektion): ComplexityMetric =
            ComplexityMetric(
                mcc = detektion.getData(complexityKey),
                cognitiveComplexity = detektion.getData(CognitiveComplexity.KEY),
                loc = detektion.getData(linesKey),
                sloc = detektion.getData(sourceLinesKey),
                lloc = detektion.getData(logicalLinesKey),
                cloc = detektion.getData(commentLinesKey),
                findings = detektion.findings.entries
            )
    }
}
