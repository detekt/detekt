package io.gitlab.arturbosch.detekt.api

/**
 * Wraps two different configuration which should be considered when retrieving properties.
 */
class CompositeConfig(private val lookFirst: Config, private val lookSecond: Config) : Config {

    override fun subConfig(key: String): Config {
        return CompositeConfig(lookFirst.subConfig(key), lookSecond.subConfig(key))
    }

    override fun <T : Any> valueOrDefault(key: String, default: T): T {
        return lookFirst.valueOrNull(key) ?: lookSecond.valueOrDefault(key, default)
    }

    override fun <T : Any> valueOrNull(key: String): T? {
        return lookFirst.valueOrNull(key) ?: lookSecond.valueOrNull(key)
    }

    override fun toString(): String = "CompositeConfig(lookFirst=$lookFirst, lookSecond=$lookSecond)"
}
