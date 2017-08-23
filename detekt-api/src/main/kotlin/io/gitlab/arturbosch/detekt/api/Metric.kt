package io.gitlab.arturbosch.detekt.api

/**
 * Metric type, can be an integer or double value. Internally it is stored as an integer,
 * but the conversion factor and is double attributes can be used to retrieve it as a double value.
 *
 * @author Artur Bosch
 */
data class Metric(val type: String,
				  val value: Int,
				  val threshold: Int,
				  val isDouble: Boolean = false,
				  val conversionFactor: Int = DEFAULT_FLOAT_CONVERSION_FACTOR) {

	constructor(type: String,
				value: Double,
				threshold: Double,
				conversionFactor: Int = DEFAULT_FLOAT_CONVERSION_FACTOR) :
			this(type, value = (value * conversionFactor).toInt(),
					threshold = (threshold * conversionFactor).toInt(),
					isDouble = true, conversionFactor = conversionFactor)

	fun doubleValue(): Double = value.convertAsDouble()
	fun doubleThreshold(): Double = threshold.convertAsDouble()

	private fun Int.convertAsDouble() = if (isDouble) this.toDouble() / conversionFactor
	else throw IllegalStateException("This metric was not marked as double!")

	override fun toString() = if (isDouble) "${doubleValue()}/${doubleThreshold()}" else "$value/$threshold"
}

/**
 * To represent a value of 0.5, use the metric value 50 and the conversion factor of 100. (50 / 100 = 0.5)
 */
val DEFAULT_FLOAT_CONVERSION_FACTOR = 100
