package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.ValueFormat.GLOB
import io.gitlab.arturbosch.detekt.api.ValueFormat.REGEX
import io.gitlab.arturbosch.detekt.api.ValueFormat.STRING

/**
 * This factory method can be used by rule authors to specify one or many configuration values along with an
 * explanation for each value. For example:
 *
 * ```kotlin
 *  @Configuration("imports which should not be used")
 *  private val imports: ValuesWithReason by config(
 *      valuesWithReason("org.junit.Test" to "Do not use Junit4. Use org.junit.jupiter.api.Test instead.")
 *  )
 * ```
 *
 * Note that the [config] property delegate only supports the factory methods when defining [ValuesWithReason].
 */
fun valuesWithReason(vararg values: Pair<String, String?>): ValuesWithReason {
    return valuesWithReason(values.map { ValueWithReason(it.first, it.second) })
}

/**
 * This factory method can be used by rule authors to specify one or many configuration values along with an
 * explanation for each value.
 *
 * Note that the [config] property delegate only supports the factory methods when defining [ValuesWithReason].
 */
fun valuesWithReason(values: List<ValueWithReason>): ValuesWithReason {
    return ValuesWithReason(values)
}

/**
 * [ValuesWithReason] is essentially the same as [List] of [ValueWithReason]. Due to type erasure we cannot use the
 * list directly. Instances of this type should always created using the [valuesWithReason] factory method.
 */
data class ValuesWithReason internal constructor(private val values: List<ValueWithReason>) :
    Iterable<ValueWithReason> by values

/**
 * A ValueWithReason represents a single configuration value that may have an explanation as to why it is used.
 * @property value the actual value that is configured
 * @property reason an optional explanation for the configured value
 * @property format the format the value should be interpreted as. Supported values are [string, regex, glob]
 */
data class ValueWithReason(val value: String, val reason: String? = null, val format: ValueFormat = STRING) {
    fun getValueAsRegex(): Regex {
        return when (format) {
            STRING -> "^${Regex.escape(value)}$".toRegex()
            REGEX -> value.toRegex()
            GLOB -> value.simplePatternToRegex() // TODO: This is not correct as it does not match the entire string
        }
    }
}

enum class ValueFormat {
    STRING, REGEX, GLOB;

    companion object {
        fun from(stringOrNull: String?): ValueFormat? {
            if (stringOrNull.isNullOrBlank()) {
                return null
            }
            val value = stringOrNull.lowercase()
            return requireNotNull(values().first { it.name.lowercase() == value }) {
                "$value is not a supported value format. Use one of ${values().toList()}."
            }
        }
    }
}
