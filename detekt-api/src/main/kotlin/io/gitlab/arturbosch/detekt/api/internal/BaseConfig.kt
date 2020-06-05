package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Config.Companion.CONFIG_SEPARATOR

/**
 * Convenient base configuration which parses/casts the configuration value based on the type of the default value.
 */
abstract class BaseConfig : Config {

    protected open fun valueOrDefaultInternal(key: String, result: Any?, default: Any): Any {
        return try {
            if (result != null) {
                when {
                    result is String -> tryParseBasedOnDefault(result, default)
                    default::class in Config.PRIMITIVES &&
                        result::class != default::class -> throw ClassCastException()
                    else -> result
                }
            } else {
                default
            }
        } catch (_: ClassCastException) {
            error("Value \"$result\" set for config parameter \"${keySequence(key)}\" is not of" +
                " required type ${default::class.simpleName}.")
        } catch (_: NumberFormatException) {
            error("Value \"$result\" set for config parameter \"${keySequence(key)}\" is not of" +
                " required type ${default::class.simpleName}.")
        }
    }

    private fun keySequence(key: String): String =
        if (parentPath == null) key else "$parentPath $CONFIG_SEPARATOR $key"

    protected open fun tryParseBasedOnDefault(result: String, defaultResult: Any): Any = when (defaultResult) {
        is Int -> result.toInt()
        is Boolean ->
            if (result in ALLOWED_BOOL_VALUES) {
                result.toBoolean()
            } else {
                throw ClassCastException()
            }
        is Double -> result.toDouble()
        is String -> result
        else -> throw ClassCastException()
    }
}

private val ALLOWED_BOOL_VALUES = setOf("true", "false")
