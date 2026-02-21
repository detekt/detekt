package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.api.Notification
import dev.detekt.core.config.validation.ValidatableConfiguration
import dev.detekt.core.config.validation.validateConfig
import dev.detekt.core.util.indentCompat
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import java.io.Reader
import kotlin.reflect.KClass
import kotlin.reflect.cast

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
        return YamlConfig(subProperties, keySequence(key), this)
    }

    override fun subConfigKeys(): Set<String> = properties.keys

    override fun <T : Any> valueOrNull(key: String, type: KClass<T>): T? {
        val value = properties[key] ?: return null
        val parsedValue = when (type) {
            Long::class if value is Int -> value.toLong()
            Float::class if (value is Int || value is Long || value is Double) -> value.toFloat()
            Double::class if (value is Int || value is Long || value is Float) -> value.toDouble()
            else -> value
        }
        return try {
            type.cast(parsedValue)
        } catch (cce: ClassCastException) {
            throw IllegalArgumentException(
                "Value '$value' set for config parameter '${keySequence(key)}' is not of required type " +
                    "`${type.qualifiedName}`",
                cce,
            )
        }
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
        private const val YAML_DOC_LIMIT = 102_400 // limit the YAML size to 100 kB

        // limit the anchors/aliases for collections to prevent attacks from for untrusted sources
        private const val ALIASES_LIMIT = 100

        /**
         * Constructs a [YamlConfig] from any [Reader].
         *
         * Note the reader will be consumed and closed.
         */
        fun load(reader: Reader): YamlConfig =
            reader.buffered().use { bufferedReader ->
                val map: Map<*, *>? = try {
                    createYamlLoad().loadFromReader(bufferedReader) as Map<*, *>?
                } catch (cce: ClassCastException) {
                    throw InvalidConfigurationError(cce)
                }
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

internal fun YamlConfig.keySequence(key: String): String = if (parentPath == null) key else "$parentPath > $key"

internal class InvalidConfigurationError(throwable: Throwable) :
    RuntimeException(
        "Provided configuration file is invalid: Structure must be from type Map<String, Any>!",
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
