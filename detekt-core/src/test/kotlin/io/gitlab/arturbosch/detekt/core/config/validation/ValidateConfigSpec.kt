package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class ValidateConfigSpec {
    private val baseline = yamlConfig("config_validation/baseline.yml")

    @Test
    fun `returns an error for an invalid config type`() {
        val invalidConfig = TestConfig()
        assertThatIllegalStateException().isThrownBy {
            validateConfig(invalidConfig, baseline, emptySet())
        }.withMessageStartingWith("Unsupported config type for validation")
    }

    @Test
    fun `returns an error for an invalid baseline`() {
        val invalidBaseline = TestConfig()
        assertThatIllegalArgumentException().isThrownBy {
            validateConfig(Config.empty, invalidBaseline, emptySet())
        }.withMessageStartingWith("Only supported baseline config is the YamlConfig.")
    }

    @Test
    fun `returns an error for an empty baseline`() {
        val invalidBaseline = Config.empty
        assertThatIllegalArgumentException().isThrownBy {
            validateConfig(Config.empty, invalidBaseline, emptySet())
        }.withMessageStartingWith("Cannot validate configuration based on an empty baseline config.")
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `converts warnings to error if config is set up this way`(warningsAsErrors: Boolean) {
        val config = yamlConfigFromContent(
            """
                config:
                  warningsAsErrors: $warningsAsErrors
                style:
                  MagicNumber:
                    ignoreNumbers: '-1,0,1,2'
            """.trimIndent()
        )

        val result = validateConfig(config, baseline, emptySet())

        val expectedLevel = if (warningsAsErrors) Notification.Level.Error else Notification.Level.Warning
        assertThat(result).anySatisfy { notification ->
            assertThat(notification.message).contains("style>MagicNumber")
            assertThat(notification.level).isEqualTo(expectedLevel)
        }
    }
}
