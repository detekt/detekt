package io.gitlab.arturbosch.detekt.api

/**
 * Metric type, can be an integer or double value. Internally it is stored as an integer,
 * but the conversion factor and is double attributes can be used to retrieve it as a double value.
 */
data class Metric(
    val type: String,
    val value: Int,
    val threshold: Int,
    val isDouble: Boolean = false,
    val conversionFactor: Int = DEFAULT_FLOAT_CONVERSION_FACTOR
) {

    constructor(
        type: String,
        value: Double,
        threshold: Double,
        conversionFactor: Int = DEFAULT_FLOAT_CONVERSION_FACTOR
    ) : this(
        type,
        value = (value * conversionFactor).toInt(),
        threshold = (threshold * conversionFactor).toInt(),
        isDouble = true,
        conversionFactor = conversionFactor
    )

    /**
     * Convenient method to retrieve the raised value as a double.
     * Internally the value is stored as an int with a conversion factor to not loose
     * any precision in calculations.
     */
    fun doubleValue(): Double = value.convertAsDouble()

    /**
     * Specified threshold for this metric as a double value.
     */
    fun doubleThreshold(): Double = threshold.convertAsDouble()

    private fun Int.convertAsDouble(): Double =
        if (isDouble) {
            this.toDouble() / conversionFactor
        } else {
            error("This metric was not marked as double!")
        }

    override fun toString(): String = if (isDouble) "${doubleValue()}/${doubleThreshold()}" else "$value/$threshold"
}

/**
 * To represent a value of 0.5, use the metric value 50 and the conversion factor of 100. (50 / 100 = 0.5)
 */
const val DEFAULT_FLOAT_CONVERSION_FACTOR: Int = 100
