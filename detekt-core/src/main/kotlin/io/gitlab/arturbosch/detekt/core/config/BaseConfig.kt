package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Config.Companion.CONFIG_SEPARATOR

private val ALLOWED_BOOL_VALUES = setOf("true", "false")

private fun Config.keySequence(key: String): String =
    if (parentPath == null) key else "$parentPath $CONFIG_SEPARATOR $key"

fun Config.valueOrDefaultInternal(
    key: String,
    result: Any?,
    default: Any,
    parser: (result: String, default: Any) -> Any = ::tryParseBasedOnDefault
): Any {
    return try {
        if (result != null) {
            when {
                result is String -> parser(result, default)
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

fun tryParseBasedOnDefault(result: String, defaultResult: Any): Any = when (defaultResult) {
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
