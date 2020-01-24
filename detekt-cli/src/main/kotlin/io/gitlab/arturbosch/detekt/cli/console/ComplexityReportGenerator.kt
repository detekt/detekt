package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion

class ComplexityReportGenerator(private val complexityMetric: ComplexityMetric) {

    private var numberOfSmells = 0
    private var smellPerThousandLines = 0
    private var mccPerThousandLines = 0
    private var commentSourceRatio = 0

    companion object Factory {
        fun create(detektion: Detektion): ComplexityReportGenerator =
            ComplexityReportGenerator(ComplexityMetric(detektion))
    }

    fun generate(): List<String>? {
        if (cannotGenerate()) return null
        return listOf(
            "${complexityMetric.loc} lines of code (loc)",
            "${complexityMetric.sloc} source lines of code (sloc)",
            "${complexityMetric.lloc} logical lines of code (lloc)",
            "${complexityMetric.cloc} comment lines of code (cloc)",
            "${complexityMetric.mcc} McCabe complexity (mcc)",
            "$numberOfSmells number of total code smells",
            "$commentSourceRatio % comment source ratio",
            "$mccPerThousandLines mcc per 1000 lloc",
            "$smellPerThousandLines code smells per 1000 lloc"
        )
    }

    private fun cannotGenerate(): Boolean {
        return when {
            complexityMetric.mcc == null -> true
            complexityMetric.lloc == null || complexityMetric.lloc == 0 -> true
            complexityMetric.sloc == null || complexityMetric.sloc == 0 -> true
            complexityMetric.cloc == null -> true
            else -> {
                numberOfSmells = complexityMetric.findings.sumBy { it.value.size }
                smellPerThousandLines = numberOfSmells * 1000 / complexityMetric.lloc
                mccPerThousandLines = complexityMetric.mcc * 1000 / complexityMetric.lloc
                commentSourceRatio = complexityMetric.cloc * 100 / complexityMetric.sloc
                false
            }
        }
    }
}
