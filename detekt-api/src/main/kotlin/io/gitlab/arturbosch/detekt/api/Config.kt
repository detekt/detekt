package io.gitlab.arturbosch.detekt.api

/**
 * A configuration holds information about how to configure specific rules.
 *
 * @author Artur Bosch
 */
interface Config {

	/**
	 * Tries to retrieve part of the configuration based on given key.
	 */
	fun subConfig(key: String): Config

	/**
	 * Retrieves a sub configuration of value based on given key. If configuration property cannot be found
	 * the specified default value is returned.
	 */
	fun <T : Any> valueOrDefault(key: String, default: () -> T): T

	/**
	 * Is thrown when loading a configuration results in errors.
	 */
	class InvalidConfigurationError(msg: String = "Provided configuration file is invalid:" +
			" Structure must be from type Map<String,Any>!") : RuntimeException(msg)

	companion object {
		/**
		 * A yaml based configuration with no properties.
		 */
		val empty: Config = YamlConfig(mapOf())
	}
}

