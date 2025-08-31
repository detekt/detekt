package dev.detekt.core.config.validation

import dev.detekt.api.Config
import dev.detekt.api.ConfigValidator
import dev.detekt.api.Notification
import dev.detekt.api.Notification.Level
import dev.detekt.core.NL
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.config.YamlConfig
import dev.detekt.core.extensions.loadExtensions
import dev.detekt.core.reporting.red
import dev.detekt.core.reporting.yellow
import dev.detekt.core.util.SimpleNotification
import dev.detekt.tooling.api.InvalidConfig

/**
 * Known existing properties on rules which may be absent in the default-detekt-config.yml.
 *
 * We need to predefine them as the user may not have already declared an 'config'-block
 * in the configuration and we want to validate the config by default.
 */
internal val DEFAULT_PROPERTY_EXCLUDES = setOf(
    ".*>excludes",
    ".*>includes",
    ".*>active",
    ".*>.*>excludes",
    ".*>.*>includes",
    ".*>.*>active",
    ".*>.*>autoCorrect",
    ".*>severity",
    ".*>.*>aliases",
    ".*>.*>severity",
    ".*>.*>ignoreAnnotated",
    ".*>.*>ignoreFunction",
).map { it.toRegex() }

internal fun checkConfiguration(settings: ProcessingSettings, baseline: Config) {
    var shouldValidate = settings.spec.configSpec.shouldValidateBeforeAnalysis
    if (shouldValidate == null) {
        val props = settings.config.subConfig("config")
        shouldValidate = props.valueOrDefault("validation", true)
    }
    if (shouldValidate) {
        val validators =
            loadExtensions<ConfigValidator>(settings) + DefaultPropertiesConfigValidator(settings, baseline)
        val notifications = validators.flatMap { it.validate(settings.config) }
        notifications.map(Notification::message).forEach(settings::info)
        val errors = notifications.filter(Notification::isError)
        if (errors.isNotEmpty()) {
            val problems = notifications.joinToString(NL) { "\t- ${it.renderMessage()}" }
            val propsString = if (errors.size == 1) "property" else "properties"
            val title = "Run failed with ${errors.size} invalid config $propsString.".red()
            throw InvalidConfig("$title$NL$problems")
        }
    }
}

internal fun validateConfig(
    config: Config,
    baseline: Config,
    excludePatterns: Set<Regex>,
): List<Notification> {
    require(baseline != Config.empty) { "Cannot validate configuration based on an empty baseline config." }
    require(baseline is YamlConfig) {
        val yamlConfigClass = YamlConfig::class.simpleName
        val actualClass = baseline.javaClass.simpleName

        "Only supported baseline config is the $yamlConfigClass. Actual type is $actualClass"
    }

    if (config == Config.empty) {
        return emptyList()
    }

    return when (config) {
        is YamlConfig -> validateYamlConfig(config, baseline, excludePatterns)
        is ValidatableConfiguration -> config.validate(baseline, excludePatterns)
        else -> error("Unsupported config type for validation: '${config::class}'.")
    }
}

private fun validateYamlConfig(
    configToValidate: YamlConfig,
    baseline: YamlConfig,
    excludePatterns: Set<Regex>,
): List<Notification> {
    val deprecatedProperties = loadDeprecations().filterIsInstance<DeprecatedProperty>().toSet()
    val warningsAsErrors = configToValidate
        .subConfig("config")
        .valueOrDefault("warningsAsErrors", false)

    val validators: List<ConfigValidator> = listOf(
        InvalidPropertiesConfigValidator(baseline, deprecatedProperties, excludePatterns),
        DeprecatedPropertiesConfigValidator(deprecatedProperties),
        MissingRulesConfigValidator(baseline, excludePatterns)
    )

    return validators
        .flatMap { it.validate(configToValidate) }
        .map { notification ->
            notification.transformIf(warningsAsErrors && notification.level == Level.Warning) {
                SimpleNotification(
                    message = notification.message,
                    level = Level.Error
                )
            }
        }
}

private fun <T> T.transformIf(condition: Boolean, transform: () -> T): T =
    if (condition) transform() else this

internal fun Notification.renderMessage(): String =
    when (level) {
        Level.Error -> message.red()
        Level.Warning -> message.yellow()
        Level.Info -> message
    }

private val Notification.isError: Boolean
    get() = level == Level.Error
