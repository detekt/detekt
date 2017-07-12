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
	fun <T : Any> valueOrDefault(key: String, default: T): T

	/**
	 * Is thrown when loading a configuration results in errors.
	 */
	class InvalidConfigurationError(msg: String = "Provided configuration file is invalid:" +
			" Structure must be from type Map<String,Any>!") : RuntimeException(msg)

	companion object {
		/**
		 * An empty configuration with no properties.
		 */
		val empty: Config = EmptyConfig
	}
}

/**
 * NOP-implementation of a config object.
 */
internal object EmptyConfig : Config {
	override fun subConfig(key: String) = this
	override fun <T : Any> valueOrDefault(key: String, default: T): T = default
}

/**
 * Convenient base configuration which parses/casts the configuration value based on the type of the default value.
 */
abstract class BaseConfig : Config {

	protected fun valueOrDefaultInternal(result: Any?, default: Any): Any {
		return try {
			if (result != null) {
				when (result) {
					is String -> tryParseBasedOnDefault(result, default)
					else -> result
				}
			} else {
				default
			}
		} catch (e: ClassCastException) {
			throw IllegalArgumentException("Type of value $result does not match the type of default value $default!")
		} catch (e: NumberFormatException) {
			throw IllegalArgumentException("Type of value $result does not match the type of default value $default!", e)
		}
	}

	private fun tryParseBasedOnDefault(result: String, defaultResult: Any): Any = when (defaultResult) {
    is Int -> java.lang.Integer.parseInt(result)
    is Boolean -> java.lang.Boolean.parseBoolean(result)
    is Double -> java.lang.Double.parseDouble(result)
    is String -> result
    else -> throw ClassCastException()
  }

}

