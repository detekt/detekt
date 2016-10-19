package io.gitlab.arturbosch.detekt.api

import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@Suppress("UNCHECKED_CAST")
class Config private constructor(val properties: Map<String, Any>) {

	fun subConfig(key: String): Config {
		val subProperties = properties.getOrElse(key) { mapOf<String, Any>() }
		return Config(subProperties as Map<String, Any>)
	}

	fun <T : Any> valueOrDefault(key: String, default: () -> T): T {
		return properties.getOrElse(key) { default() } as T
	}

	internal class InvalidConfigurationError(msg: String = "Provided configuration file is invalid:" +
			" Structure must be of type Map<String,Any>!") : RuntimeException(msg)

	companion object {
		fun load(path: Path): Config {
			require(Files.exists(path) && path.toString().endsWith("yml"))
			return Files.newBufferedReader(path).use {
				val map = Yaml().loadAll(it).iterator().next()
				if (map is Map<*, *>) {
					Config(map as Map<String, Any>)
				} else {
					throw InvalidConfigurationError()
				}
			}
		}

		val EMPTY: Config = Config(mapOf())
	}
}

