package io.gitlab.arturbosch.detekt.core.config.validation

import dev.detekt.api.Notification
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import dev.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DeprecatedPropertiesConfigValidatorSpec {
    private val deprecatedProperties = setOf(
        DeprecatedProperty(
            ruleSetId = "naming",
            ruleName = "FunctionParameterNaming",
            propertyName = "ignoreOverriddenFunctions",
            description = "Use `ignoreOverridden` instead"
        )
    )

    private val subject = DeprecatedPropertiesConfigValidator(deprecatedProperties)

    @Test
    fun `reports a deprecated property as a warning`() {
        val settings = ValidationSettings()
        val config = yamlConfigFromContent(
            """
                naming:
                  FunctionParameterNaming:
                    ignoreOverriddenFunctions: ''
            """.trimIndent()
        ) as YamlConfig

        val result = subject.validate(config, settings)

        assertThat(result).anySatisfy { notification ->
            assertThat(notification.level)
                .isEqualTo(Notification.Level.Warning)
            assertThat(notification.message)
                .contains("naming>FunctionParameterNaming>ignoreOverriddenFunctions")
                .contains("Use `ignoreOverridden` instead")
        }
    }
}
