package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */
interface Config {

	fun subConfig(key: String): Config

	fun <T : Any> valueOrDefault(key: String, default: () -> T): T

	class InvalidConfigurationError(msg: String = "Provided configuration file is invalid:" +
			" Structure must be from type Map<String,Any>!") : RuntimeException(msg)

	companion object {
		val empty: Config = YamlConfig(mapOf())
	}
}

