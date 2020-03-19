@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Config.Companion.CONFIG_SEPARATOR
import io.gitlab.arturbosch.detekt.api.HierarchicalConfig
import io.gitlab.arturbosch.detekt.api.Notification
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.net.URL
import java.nio.file.Path

/**
 * Config implementation using the yaml format. SubConfigurations can return sub maps according to the
 * yaml specification.
 */
class YamlConfig internal constructor(
    val properties: Map<String, Any>,
    override val parent: HierarchicalConfig.Parent?,
    override val parentPath: String? = null
) : BaseConfig(), ValidatableConfiguration {

    override fun subConfig(key: String): Config {
        val subProperties = properties.getOrElse(key) { mapOf<String, Any>() }
        return YamlConfig(
            subProperties as Map<String, Any>,
            HierarchicalConfig.Parent(this, key),
            if (parentPath == null) key else "$parentPath $CONFIG_SEPARATOR $key"
        )
    }

    override fun <T : Any> valueOrDefault(key: String, default: T): T {
        val result = properties[key]
        return valueOrDefaultInternal(key, result, default) as T
    }

    override fun <T : Any> valueOrNull(key: String): T? {
        return properties[key] as? T?
    }

    override fun toString(): String {
        return "YamlConfig(properties=$properties)"
    }

    override fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification> =
        validateConfig(this, baseline, excludePatterns)

    companion object {

        /**
         * Factory method to load a yaml configuration. Given path must exist
         * and point to a readable file.
         */
        fun load(path: Path): Config =
            load(path.toFile().apply {
                require(exists()) { "Configuration does not exist: $path" }
                require(isFile) { "Configuration must be a file: $path" }
                require(canRead()) { "Configuration must be readable: $path" }
            }.bufferedReader())

        /**
         * Factory method to load a yaml configuration from a URL.
         */
        fun loadResource(url: URL): Config = load(url.openStream().bufferedReader())

        private fun load(reader: BufferedReader): Config = reader.use {
            val yamlInput = it.lineSequence().joinToString("\n")
            if (yamlInput.isEmpty()) {
                Config.empty
            } else {
                val map: Any = Yaml().load(yamlInput)
                if (map is Map<*, *>) {
                    YamlConfig(map as Map<String, Any>, parent = null)
                } else {
                    throw Config.InvalidConfigurationError(
                        "Provided configuration file is invalid: Structure must be of type 'Map<String,Any>'."
                    )
                }
            }
        }
    }
}
