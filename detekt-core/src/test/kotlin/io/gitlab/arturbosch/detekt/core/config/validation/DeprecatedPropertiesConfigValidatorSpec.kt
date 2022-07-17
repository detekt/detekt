package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class DeprecatedPropertiesConfigValidatorSpec {

    private val subject = DeprecatedPropertiesConfigValidator()

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `reports a deprecated property as a warning or error`(warningsAsErrors: Boolean) {
        val settings = ValidationSettings(warningsAsErrors = warningsAsErrors)
        val config = yamlConfigFromContent(
            """
                naming:
                  FunctionParameterNaming:
                    ignoreOverriddenFunctions: ''
            """.trimIndent()
        ) as YamlConfig

        val result = subject.validate(config, settings)

        assertThat(result).hasSize(1)
        val notification = result.first()
        assertThat(notification.isError).isEqualTo(warningsAsErrors)
        assertThat(notification.message).contains("naming>FunctionParameterNaming>ignoreOverriddenFunctions")
        assertThat(notification.message).contains("Use `ignoreOverridden` instead")
    }
}
