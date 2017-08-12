package io.gitlab.arturbosch.detekt.api

/**
 * Anything that can be expressed as a number value for projects.
 *
 * @author Artur Bosch
 */
open class ProjectMetric(val type: String,
						 val value: Int,
						 val priority: Int = -1,
						 val isDouble: Boolean = false,
						 val conversionFactor: Int = DEFAULT_FLOAT_CONVERSION_FACTOR) {

	override fun toString(): String = "$type: ${if (isDouble)
		(value.toDouble() / conversionFactor).toString() else value.toString()}"
}
