package dev.detekt.api

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

    /**
     * The reference to a parent [Config] from this configuration, useful when navigating with [subConfig].
     * It's `null` if this is the top most configuration object.
     */
    val parent: Config?

    /**
     * Tries to retrieve part of the configuration based on given key.
     */
    fun subConfig(key: String): Config

    /**
     * Returns a set of all sub configuration keys.
     */
    fun subConfigKeys(): Set<String>

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

    companion object {

        /**
         * An empty configuration with no properties.
         * This config should only be used in test cases.
         * Always returns the default value except when 'active' is queried, it returns true.
         */
        val empty: Config = object : Config {
            override val parentPath: String? = null

            override val parent: Config = this

            override fun subConfig(key: String): Config = this

            override fun subConfigKeys(): Set<String> = emptySet()

            override fun <T : Any> valueOrNull(key: String): T? = null

            override fun toString(): String = "Config.empty"
        }

        const val ACTIVE_KEY: String = "active"
        const val ALIASES_KEY: String = "aliases"
        const val AUTO_CORRECT_KEY: String = "autoCorrect"
        const val SEVERITY_KEY: String = "severity"
        const val EXCLUDES_KEY: String = "excludes"
        const val INCLUDES_KEY: String = "includes"
        const val CONFIG_SEPARATOR: String = ">"
    }
}
