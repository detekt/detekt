package io.gitlab.arturbosch.detekt.cli.config

import io.gitlab.arturbosch.detekt.api.ConfigValidator
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.cli.console.red
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.util.ServiceLoader

fun loadValidators(settings: ProcessingSettings): List<ConfigValidator> =
    ServiceLoader.load(ConfigValidator::class.java, settings.pluginLoader)
        .onEach { it.init(settings.config); it.init(settings) }
        .toList()

fun checkConfiguration(settings: ProcessingSettings) {
    val props = settings.config.subConfig("config")
    val shouldValidate = props.valueOrDefault("validation", true)

    if (shouldValidate) {
        val validators = loadValidators(settings) + DefaultPropertiesConfigValidator(settings)
        val notifications = validators.flatMap { it.validate(settings.config) }
        notifications.map(Notification::message).forEach(settings::info)
        val errors = notifications.filter(Notification::isError)
        if (errors.isNotEmpty()) {
            val propsString = if (errors.size == 1) "property" else "properties"
            throw InvalidConfig("Run failed with ${errors.size} invalid config $propsString.".red())
        }
    }
}
