package io.gitlab.arturbosch.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.CONFIG_SEPARATOR
import kotlin.reflect.KClass

private val ALLOWED_BOOL_VALUES = setOf("true", "false")

private fun Config.keySequence(key: String): String =
    if (parentPath == null) key else "$parentPath $CONFIG_SEPARATOR $key"

fun Config.valueOrDefaultInternal(
    key: String,
    result: Any?,
    default: Any,
    parser: (result: String, default: Any) -> Any = ::tryParseBasedOnDefault,
): Any =
    try {
        if (result != null) {
            when {
                result is String -> parser(result, default)
                result is List<*> -> {
                    if (default !is List<*>) {
                        throw ClassCastException()
                    }
                    check(result.all { it is String }) {
                        "Only lists of strings are supported. Value \"$result\" set " +
                            "for config parameter \"${keySequence(key)}\" contains non-string values."
                    }
                    result.map { it as String }
                }

                default::class in PRIMITIVES &&
                    result::class != default::class -> throw ClassCastException()

                else -> result
            }
        } else {
            default
        }
    } catch (_: ClassCastException) {
        error(
            "Value \"$result\" set for config parameter \"${keySequence(key)}\" is not of" +
                " required type ${default::class.simpleName?.let { getDefaultName(it) }}."
        )
    } catch (_: NumberFormatException) {
        error(
            "Value \"$result\" set for config parameter \"${keySequence(key)}\" is not of" +
                " required type ${default::class.simpleName?.let { getDefaultName(it) }}."
        )
    }

private fun getDefaultName(className: String): String = if (className == "EmptyList") {
    "List"
} else {
    className
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

private val PRIMITIVES: Set<KClass<out Any>> = setOf(
    Int::class,
    Boolean::class,
    Float::class,
    Double::class,
    String::class,
    Short::class,
    Char::class,
    Long::class
)
