package dev.detekt.test

import dev.detekt.api.Config
import dev.detekt.api.ValueWithReason

class TestConfig private constructor(override val parent: Config?, private val values: Map<String, Any>) : Config {
    constructor(parent: Config?, vararg pairs: Pair<String, Any>) : this(parent, pairs.toMap())

    constructor(vararg pairs: Pair<String, Any>) : this(Config.empty, *pairs)

    override fun subConfig(key: String): TestConfig {
        @Suppress("UNCHECKED_CAST")
        val value = values.getOrDefault(key, emptyMap<String, Any>()) as Map<String, Any>
        return TestConfig(this, value)
    }

    override fun subConfigKeys(): Set<String> = values.keys

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> valueOrDefault(key: String, default: T) = values.getOrDefault(key, default) as T

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> valueOrNull(key: String): T? = values[key] as? T
}

fun ValueWithReason.toConfig(): Map<String, String?> = mapOf("value" to value, "reason" to reason)
