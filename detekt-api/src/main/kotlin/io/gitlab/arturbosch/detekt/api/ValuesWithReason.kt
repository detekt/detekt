package io.gitlab.arturbosch.detekt.api

fun valuesWithReason(vararg values: Pair<String, String?>): ValuesWithReason {
    return ValuesWithReason(values.map { ValueWithReason(it.first, it.second) })
}

data class ValuesWithReason(val values: List<ValueWithReason>) : List<ValueWithReason> by values

data class ValueWithReason(val value: String, val reason: String? = null)
