package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.api.Notification
import dev.detekt.core.config.validation.ValidatableConfiguration
import dev.detekt.core.config.validation.validateConfig
import dev.detekt.core.util.indentCompat
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import java.io.Reader
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isReadable
import kotlin.io.path.isRegularFile
import kotlin.io.path.reader

/**
 * Config implementation using the yaml format. SubConfigurations can return sub maps according to the
 * yaml specification.
 */
class YamlConfig internal constructor(
    val properties: Map<String, Any>,
    val parentPath: String?,
    override val parent: Config?,
) : Config,
    ValidatableConfiguration {

    override fun subConfig(key: String): YamlConfig {
        @Suppress("UNCHECKED_CAST")
        val subProperties = properties.getOrElse(key) { emptyMap<String, Any>() } as Map<String, Any>
        return YamlConfig(
            subProperties,
            if (parentPath == null) key else "$parentPath $CONFIG_SEPARATOR $key",
            this,
        )
    }

    override fun subConfigKeys(): Set<String> = properties.keys

    override fun <T : Any> valueOrDefault(key: String, default: T): T {
        val result = properties[key]
        @Suppress("UNCHECKED_CAST")
        return valueOrDefaultInternal(key, result, default) as T
    }

    override fun <T : Any> valueOrNull(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return properties[key] as? T?
    }

    @Suppress("MagicNumber")
    override fun toString() =
        """
            YamlConfig(
                ${properties.toPrettyString(recursive = 1).indentCompat(12).trim()},
            )
        """.trimIndent()

    override fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification> =
        validateConfig(this, baseline, excludePatterns)

    companion object {
        const val CONFIG_SEPARATOR: String = ">"
        private const val YAML_DOC_LIMIT = 102_400 // limit the YAML size to 100 kB

        // limit the anchors/aliases for collections to prevent attacks from for untrusted sources
        private const val ALIASES_LIMIT = 100

        /**
         * Factory method to load a yaml configuration. Given path must exist and point to a readable file.
         */
        fun load(path: Path): YamlConfig {
            require(path.exists()) { "Configuration does not exist: $path" }
            require(path.isRegularFile()) { "Configuration must be a file: $path" }
            require(path.isReadable()) { "Configuration must be readable: $path" }

            return load(path.reader())
        }

        /**
         * Constructs a [YamlConfig] from any [Reader].
         *
         * Note the reader will be consumed and closed.
         */
        fun load(reader: Reader): YamlConfig =
            reader.buffered().use { bufferedReader ->
                val map: Map<*, *>? = runCatching {
                    @Suppress("UNCHECKED_CAST")
                    createYamlLoad().loadFromReader(bufferedReader) as Map<String, *>?
                }.getOrElse { throw InvalidConfigurationError(it) }
                @Suppress("UNCHECKED_CAST")
                YamlConfig(map.orEmpty() as Map<String, Any>, null, null)
            }

        private fun createYamlLoad() =
            Load(
                LoadSettings.builder()
                    .setAllowDuplicateKeys(false)
                    .setAllowRecursiveKeys(false)
                    .setCodePointLimit(YAML_DOC_LIMIT)
                    .setMaxAliasesForCollections(ALIASES_LIMIT)
                    .build()
            )
    }
}

internal class InvalidConfigurationError(throwable: Throwable) :
    RuntimeException(
        """
            Provided configuration file is invalid: Structure must be from type Map<String,Any>!
            ${throwable.message}
        """.trimIndent(),
        throwable,
    )

@Suppress("MagicNumber")
private fun Map<*, *>.toPrettyString(recursive: Int): String =
    if (isEmpty()) {
        "{}"
    } else {
        toList()
            .joinToString(separator = ",\n    ", prefix = "{\n    ", postfix = ",\n}") { (key, value) ->
                when {
                    recursive == 0 -> "$key=$value"
                    value is Map<*, *> -> "$key=${value.toPrettyString(recursive - 1).indentCompat(4).trim()}"
                    else -> "$key=$value"
                }
            }
    }
