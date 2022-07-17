package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Test

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
}
