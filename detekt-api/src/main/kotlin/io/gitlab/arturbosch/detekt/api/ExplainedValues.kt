package io.gitlab.arturbosch.detekt.api

fun explainedValues(vararg values: Pair<String, String?>): ExplainedValues {
    return ExplainedValues(values.map { ExplainedValue(it.first, it.second) })
}

data class ExplainedValues(val values: List<ExplainedValue>) : List<ExplainedValue> by values

data class ExplainedValue(val value: String, val reason: String? = null)
