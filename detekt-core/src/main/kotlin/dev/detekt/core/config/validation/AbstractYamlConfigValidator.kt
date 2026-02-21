package dev.detekt.core.config.validation

import dev.detekt.api.Config
import dev.detekt.api.ConfigValidator
import dev.detekt.api.Notification
import dev.detekt.api.valueOrDefault
import dev.detekt.core.config.YamlConfig

internal abstract class AbstractYamlConfigValidator : ConfigValidator {

    override fun validate(config: Config): Collection<Notification> {
        require(config is YamlConfig) {
            val yamlConfigClass = YamlConfig::class.simpleName
            val actualClass = config.javaClass.simpleName

            "Only supported config is the $yamlConfigClass. Actual type is $actualClass"
        }
        val settings = ValidationSettings(
            config.subConfig("config").valueOrDefault("checkExhaustiveness", false),
        )

        return validate(config, settings)
    }

    abstract fun validate(configToValidate: YamlConfig, settings: ValidationSettings): Collection<Notification>
}
