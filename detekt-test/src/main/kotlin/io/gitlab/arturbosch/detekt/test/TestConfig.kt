package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.BaseConfig
import io.gitlab.arturbosch.detekt.api.HierarchicalConfig

/**
 * @author Artur Bosch
 */
@Suppress("UNCHECKED_CAST")
open class TestConfig(
    private val values: Map<String, String> = mutableMapOf(),
    override val parent: HierarchicalConfig.Parent? = null
) : BaseConfig() {

    override fun subConfig(key: String) = this

    override fun <T : Any> valueOrDefault(key: String, default: T) = when (key) {
        "active" -> getActiveValue(default) as T
        else -> valueOrDefaultInternal(key, values[key], default) as T
    }

    private fun <T : Any> getActiveValue(default: T): Any {
        val active = values["active"]
        return if (active != null) valueOrDefaultInternal("active", active, default) else true
    }

    override fun <T : Any> valueOrNull(key: String): T? = when (key) {
        "active" -> (values["active"] ?: true) as T?
        else -> values[key] as? T
    }

    override fun tryParseBasedOnDefault(result: String, defaultResult: Any): Any = when (defaultResult) {
        is List<*> -> parseList(result)
        is Set<*> -> parseList(result).toSet()
        else -> super.tryParseBasedOnDefault(result, defaultResult)
    }

    protected fun parseList(result: String): List<String> {
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
