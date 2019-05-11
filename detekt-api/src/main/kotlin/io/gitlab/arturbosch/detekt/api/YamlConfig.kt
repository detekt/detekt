package io.gitlab.arturbosch.detekt.api

import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

/**
 * Config implementation using the yaml format. SubConfigurations can return sub maps according to the
 * yaml specification.
 *
 * @author Artur Bosch
 */
@Suppress("UNCHECKED_CAST")
class YamlConfig internal constructor(
    val properties: Map<String, Any>,
    override val parent: HierarchicalConfig.Parent?
) : BaseConfig() {

    override fun subConfig(key: String): Config {
        val subProperties = properties.getOrElse(key) { mapOf<String, Any>() }
        return YamlConfig(subProperties as Map<String, Any>, HierarchicalConfig.Parent(this, key))
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

    companion object {

        private const val YAML = ".yml"

        /**
         * Factory method to load a yaml configuration. Given path must exist and end with "yml".
         */
        fun load(path: Path): Config {
            require(Files.exists(path)) { "File does not exist!" }
            require(path.toString().endsWith(YAML)) { "File does not end with $YAML!" }
            return load(Files.newBufferedReader(path))
        }

        /**
         * Factory method to load a yaml configuration from a URL.
         */
        fun loadResource(url: URL): Config {
            val reader = url.openStream().bufferedReader()
            return load(reader)
        }

        private fun load(reader: BufferedReader): Config = reader.use {
            val yamlInput = it.readText()
            if (yamlInput.isEmpty()) {
                Config.empty
            } else {
                val map: Any = Yaml().load(yamlInput)
                if (map is Map<*, *>) {
                    YamlConfig(map as Map<String, Any>, parent = null)
                } else {
                    throw Config.InvalidConfigurationError()
                }
            }
        }

        private fun BufferedReader.readText() = lineSequence().joinToString("\n")
    }
}
