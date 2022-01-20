package io.gitlab.arturbosch.detekt.api

interface ExplainedValues : Iterable<ExplainedValue> {
    val values: List<ExplainedValue>
    override operator fun iterator(): Iterator<ExplainedValue> = values.iterator()
}

interface ExplainedValue {
    val value: String
    val reason: String?
}

fun explainedValues(vararg values: Pair<String, String>): ExplainedValues {
    return ExplainedValuesImpl(values = values.map { ExplainedValueImpl(it.first, it.second) })
}

internal data class ExplainedValuesImpl(override val values: List<ExplainedValue>) : ExplainedValues

internal data class ExplainedValueImpl(override val value: String, override val reason: String? = null) : ExplainedValue
