package io.gitlab.arturbosch.detekt.api

import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@Suppress("UNCHECKED_CAST")
class YamlConfig internal constructor(val properties: Map<String, Any>) : Config {

	override fun subConfig(key: String): Config {
		val subProperties = properties.getOrElse(key) { mapOf<String, Any>() }
		return YamlConfig(subProperties as Map<String, Any>)
	}

	override fun <T : Any> valueOrDefault(key: String, default: () -> T): T {
		return properties.getOrElse(key) { default() } as T
	}

	companion object {
		fun load(path: Path): Config {
			require(Files.exists(path) && path.toString().endsWith("yml"))
			return Files.newBufferedReader(path).use {
				val map = Yaml().loadAll(it).iterator().next()
				if (map is Map<*, *>) {
					YamlConfig(map as Map<String, Any>)
				} else {
					throw Config.InvalidConfigurationError()
				}
			}
		}
	}
}