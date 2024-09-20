package io.gitlab.arturbosch.detekt.api

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
fun valuesWithReason(vararg values: Pair<String, String?>): ValuesWithReason =
    valuesWithReason(values.map { ValueWithReason(it.first, it.second) })

/**
 * This factory method can be used by rule authors to specify one or many configuration values along with an
 * explanation for each value.
 *
 * Note that the [config] property delegate only supports the factory methods when defining [ValuesWithReason].
 */
fun valuesWithReason(values: List<ValueWithReason>): ValuesWithReason = ValuesWithReason(values)

/**
 * [ValuesWithReason] is essentially the same as [List] of [ValueWithReason]. Due to type erasure we cannot use the
 * list directly. Instances of this type should always created using the [valuesWithReason] factory method.
 */
@ExposedCopyVisibility
data class ValuesWithReason internal constructor(private val values: List<ValueWithReason>) :
    Iterable<ValueWithReason> by values

/**
 * A ValueWithReason represents a single configuration value that may have an explanation as to why it is used.
 * @property value the actual value that is configured
 * @property reason an optional explanation for the configured value
 */
data class ValueWithReason(val value: String, val reason: String? = null)
