@file:Suppress("unused")

package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.EmptyConfig
import kotlin.reflect.KClass

/**
 * A configuration holds information about how to configure specific rules.
 */
interface Config {

    /**
     * Keeps track of which key was taken to [subConfig] this configuration.
     * Sub-sequential calls to [subConfig] are tracked with '>' as a separator.
     *
     * May be null if this is the top most configuration object.
     */
    val parentPath: String?
        get() = null

    /**
     * Tries to retrieve part of the configuration based on given key.
     */
    fun subConfig(key: String): Config

    /**
     * Retrieves a sub configuration or value based on given key. If configuration property cannot be found
     * the specified default value is returned.
     */
    fun <T : Any> valueOrDefault(key: String, default: T): T = valueOrNull(key) ?: default

    /**
     * Retrieves a sub configuration or value based on given key.
     * If the configuration property cannot be found, null is returned.
     */
    fun <T : Any> valueOrNull(key: String): T?

    /**
     * Is thrown when loading a configuration results in errors.
     */
    @Deprecated("Default value of parameter 'msg' applies only to YamlConfig and will be removed in the future")
    class InvalidConfigurationError(
        msg: String = "Provided configuration file is invalid:" +
            " Structure must be from type Map<String,Any>!"
    ) : RuntimeException(msg)

    companion object {

        /**
         * An empty configuration with no properties.
         * This config should only be used in test cases.
         * Always returns the default value except when 'active' is queried, it returns true .
         */
        val empty: Config = EmptyConfig

        const val ACTIVE_KEY: String = "active"
        const val EXCLUDES_KEY: String = "excludes"
        const val INCLUDES_KEY: String = "includes"
        const val CONFIG_SEPARATOR: String = ">"

        val PRIMITIVES: Set<KClass<out Any>> = setOf(
            Int::class,
            Boolean::class,
            Float::class,
            Double::class,
            String::class,
            Short::class,
            Char::class,
            Long::class
        )
    }
}

/**
 * A configuration which keeps track of the config it got sub-config'ed from by the [subConfig] function.
 * It's main usage is to recreate the property-path which was taken when using the [subConfig] function repeatedly.
 */
@Deprecated("""
A Config is a long lived object and is derived via subConfig a lot.
Keeping track of the parent it was derived, creates long-lived object chains which takes the GC longer to release them.
It can even lead to OOM if detekt get's embedded in an other application which reuses the top most Config object. 
The property 'parentPath' of the Config interface can be used as a replacement for parent.key calls.
""")
interface HierarchicalConfig : Config {
    /**
     * Returns the parent config which encloses this config part.
     */
    val parent: Parent?

    /**
     * Keeps track of which key was taken to [subConfig] this configuration.
     */
    data class Parent(val config: Config, val key: String)
}

/**
 * Convenient base configuration which parses/casts the configuration value based on the type of the default value.
 */
@Deprecated("'BaseConfig' exposes implementation details of 'YamlConfig' and should't be relied on.")
typealias BaseConfig = io.gitlab.arturbosch.detekt.api.internal.BaseConfig
