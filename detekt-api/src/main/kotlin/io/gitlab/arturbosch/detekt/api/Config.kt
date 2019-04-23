package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.Config.Companion.PRIMITIVES
import java.util.LinkedList
import kotlin.reflect.KClass

/**
 * A configuration holds information about how to configure specific rules.
 *
 * @author Artur Bosch
 * @author schalkms
 */
interface Config {

    /**
     * Tries to retrieve part of the configuration based on given key.
     */
    fun subConfig(key: String): Config

    /**
     * Retrieves a sub configuration or value based on given key. If configuration property cannot be found
     * the specified default value is returned.
     */
    fun <T : Any> valueOrDefault(key: String, default: T): T

    /**
     * Retrieves a sub configuration or value based on given key.
     * If the configuration property cannot be found, null is returned.
     */
    fun <T : Any> valueOrNull(key: String): T?

    /**
     * Is thrown when loading a configuration results in errors.
     */
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

        const val EXCLUDES_KEY = "excludes"
        const val INCLUDES_KEY = "includes"

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

interface HierarchicalConfig : Config {
    /**
     * Returns the parent config which encloses this config part.
     */
    val parent: Parent?

    data class Parent(val config: Config, val key: String)
}

/**
 * NOP-implementation of a config object.
 */
internal object EmptyConfig : HierarchicalConfig {

    override val parent: HierarchicalConfig.Parent? = null

    override fun subConfig(key: String) = this

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> valueOrDefault(key: String, default: T): T = when (key) {
        "active" -> true as T
        else -> default
    }

    override fun <T : Any> valueOrNull(key: String): T? = null
}

/**
 * Convenient base configuration which parses/casts the configuration value based on the type of the default value.
 */
abstract class BaseConfig : HierarchicalConfig {

    protected open fun valueOrDefaultInternal(key: String, result: Any?, default: Any): Any {
        return try {
            if (result != null) {
                when {
                    result is String -> tryParseBasedOnDefault(result, default)
                    default::class in PRIMITIVES &&
                        result::class != default::class -> throw ClassCastException()
                    else -> result
                }
            } else {
                default
            }
        } catch (e: ClassCastException) {
            error("Value \"$result\" set for config parameter \"${keySequence(key)}\" is not of required type ${default::class.simpleName}.")
        } catch (_: NumberFormatException) {
            error("Value \"$result\" set for config parameter \"${keySequence(key)}\" is not of required type ${default::class.simpleName}.")
        }
    }

    private fun keySequence(key: String): String {
        val seq = LinkedList<String>()
        var current = parent
        while (current != null) {
            seq.addFirst(current.key)
            current = (current.config as? HierarchicalConfig)?.parent
        }
        val keySeq = seq.joinToString(" > ")
        return if (keySeq.isEmpty()) key else "$keySeq > $key"
    }

    protected open fun tryParseBasedOnDefault(result: String, defaultResult: Any): Any = when (defaultResult) {
        is Int -> result.toInt()
        is Boolean -> result.toBoolean()
        is Double -> result.toDouble()
        is String -> result
        else -> throw ClassCastException()
    }
}
