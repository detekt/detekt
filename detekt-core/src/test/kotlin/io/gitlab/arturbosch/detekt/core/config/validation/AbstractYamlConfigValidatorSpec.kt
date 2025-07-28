package io.gitlab.arturbosch.detekt.core.config.validation

import dev.detekt.api.Notification
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import dev.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class AbstractYamlConfigValidatorSpec {

    @Test
    fun `use default validation settings`() {
        val validator = SettingsCapturingValidatorAbstract()
        val config = yamlConfigFromContent("")

        validator.validate(config)

        assertThat(validator.validationSettings.checkExhaustiveness).isFalse()
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `extract checkExhaustiveness settings from config`(configValue: Boolean) {
        val config = yamlConfigFromContent(
            """
                config:
                  checkExhaustiveness: $configValue
            """.trimIndent()
        )
        val validator = SettingsCapturingValidatorAbstract()

        validator.validate(config)

        assertThat(validator.validationSettings.checkExhaustiveness).isEqualTo(configValue)
    }

    private class SettingsCapturingValidatorAbstract : AbstractYamlConfigValidator() {

        override val id: String = "SettingsCapturingValidatorAbstract"

        lateinit var validationSettings: ValidationSettings

        override fun validate(
            configToValidate: YamlConfig,
            settings: ValidationSettings,
        ): Collection<Notification> {
            validationSettings = settings
            return emptyList()
        }
    }
}
