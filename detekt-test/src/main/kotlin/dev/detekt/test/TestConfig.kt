package dev.detekt.test

import dev.detekt.api.Config
import dev.detekt.api.ValueWithReason
import dev.detekt.core.config.tryParseBasedOnDefault
import dev.detekt.core.config.valueOrDefaultInternal

@Suppress("UNCHECKED_CAST")
class TestConfig private constructor(override val parent: Config?, private val values: Map<String, Any>) : Config {
    override val parentPath: String? = null

    constructor(parent: Config?, vararg pairs: Pair<String, Any>) : this(parent, pairs.toMap())

    constructor(vararg pairs: Pair<String, Any>) : this(Config.empty, *pairs)

    override fun subConfig(key: String) =
        TestConfig(
            this,
            *values.map { (keyInValues, value) -> keyInValues to value }.toTypedArray()
        )

    override fun subConfigKeys(): Set<String> = values.keys

    override fun <T : Any> valueOrDefault(key: String, default: T) =
        if (key == Config.ACTIVE_KEY) {
            getActiveValue(default) as T
        } else {
            valueOrDefaultInternal(key, values[key], default, ::tryParseBasedOnDefaultRespectingCollections) as T
        }

    private fun <T : Any> getActiveValue(default: T): Any {
        val active = values[Config.ACTIVE_KEY]
        return if (active != null) {
            valueOrDefaultInternal("active", active, default, ::tryParseBasedOnDefaultRespectingCollections)
        } else {
            true
        }
    }

    override fun <T : Any> valueOrNull(key: String): T? =
        if (key == Config.ACTIVE_KEY) {
            (values[Config.ACTIVE_KEY] ?: true) as T?
        } else {
            values[key] as? T
        }

    private fun tryParseBasedOnDefaultRespectingCollections(result: String, defaultResult: Any): Any =
        when (defaultResult) {
            is List<*> -> parseList(result)
            is Set<*> -> parseList(result).toSet()
            else -> tryParseBasedOnDefault(result, defaultResult)
        }

    private fun parseList(result: String): List<String> {
        if (result.startsWith('[') && result.endsWith(']')) {
            val str = result.substring(1, result.length - 1)
            return str.splitToSequence(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toList()
        }
        throw ClassCastException()
    }
}

fun ValueWithReason.toConfig(): Map<String, String?> = mapOf("value" to value, "reason" to reason)
