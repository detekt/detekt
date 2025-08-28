package dev.detekt.api

import dev.drewhamilton.poko.Poko

/**
 * Anything that can be expressed as a numeric value for projects.
 */
@Poko
open class ProjectMetric(
    val type: String,
    val value: Int,
    val priority: Int = -1,
    val isDouble: Boolean = false,
    val conversionFactor: Int = DEFAULT_FLOAT_CONVERSION_FACTOR,
) {

    override fun toString(): String {
        val stringValue = if (isDouble) {
            (value.toDouble() / conversionFactor).toString()
        } else {
            value.toString()
        }
        return "$type: $stringValue"
    }
}

private const val DEFAULT_FLOAT_CONVERSION_FACTOR = 100
