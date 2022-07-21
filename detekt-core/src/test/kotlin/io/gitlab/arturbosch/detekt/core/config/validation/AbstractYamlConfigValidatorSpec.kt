package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class AbstractYamlConfigValidatorSpec {

    @Test
    fun `use default validation settings and given exclude patterns`() {
        val excludePatterns = setOf(".*".toRegex())
        val validator = SettingsCapturingValidatorAbstract(excludePatterns)
        val config = yamlConfigFromContent("")

        validator.validate(config)

        assertThat(validator.validationSettings.checkExhaustiveness).isFalse()
        assertThat(validator.excludePatterns).isEqualTo(excludePatterns)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `extract checkExhaustiveness settings from config`(configValue: Boolean) {
        val excludePatterns = emptySet<Regex>()
        val config = yamlConfigFromContent(
            """
                config:
                  checkExhaustiveness: $configValue
            """.trimIndent()
        )
        val validator = SettingsCapturingValidatorAbstract(excludePatterns)

        validator.validate(config)

        assertThat(validator.validationSettings.checkExhaustiveness).isEqualTo(configValue)
    }

    private class SettingsCapturingValidatorAbstract(
        excludePatterns: Set<Regex>,
    ) : AbstractYamlConfigValidator(excludePatterns) {
        lateinit var validationSettings: ValidationSettings
        override fun validate(
            configToValidate: YamlConfig,
            settings: ValidationSettings
        ): Collection<Notification> {
            validationSettings = settings
            return emptyList()
        }
    }
}
