package io.github.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Detektion
import java.util.Locale

class ComplexityReportGenerator(private val complexityMetric: ComplexityMetric) {

    private var numberOfSmells = 0
    private var smellPerThousandLines = 0
    private var mccPerThousandLines = 0
    private var commentSourceRatio = 0

    fun generate(): List<String>? {
        if (cannotGenerate()) return null
        return listOf(
            "%,d lines of code (loc)".format(Locale.US, complexityMetric.loc),
            "%,d source lines of code (sloc)".format(Locale.US, complexityMetric.sloc),
            "%,d logical lines of code (lloc)".format(Locale.US, complexityMetric.lloc),
            "%,d comment lines of code (cloc)".format(Locale.US, complexityMetric.cloc),
            "%,d cyclomatic complexity (mcc)".format(Locale.US, complexityMetric.mcc),
            "%,d cognitive complexity".format(Locale.US, complexityMetric.cognitiveComplexity),
            "%,d number of total code smells".format(Locale.US, numberOfSmells),
            "%,d%% comment source ratio".format(Locale.US, commentSourceRatio),
            "%,d mcc per 1,000 lloc".format(Locale.US, mccPerThousandLines),
            "%,d code smells per 1,000 lloc".format(Locale.US, smellPerThousandLines)
        )
    }

    private fun cannotGenerate(): Boolean {
        return when {
            null in setOf(
                complexityMetric.mcc,
                complexityMetric.cloc,
                complexityMetric.cognitiveComplexity
            ) -> true
            complexityMetric.lloc == null || complexityMetric.lloc == 0 -> true
            complexityMetric.sloc == null || complexityMetric.sloc == 0 -> true
            else -> {
                numberOfSmells = complexityMetric.findings.sumBy { it.value.size }
                smellPerThousandLines = numberOfSmells * 1000 / complexityMetric.lloc
                mccPerThousandLines = requireNotNull(complexityMetric.mcc) * 1000 / complexityMetric.lloc
                commentSourceRatio = requireNotNull(complexityMetric.cloc) * 100 / complexityMetric.sloc
                false
            }
        }
    }

    companion object Factory {
        fun create(detektion: Detektion): ComplexityReportGenerator =
            ComplexityReportGenerator(ComplexityMetric(detektion))
    }
}
