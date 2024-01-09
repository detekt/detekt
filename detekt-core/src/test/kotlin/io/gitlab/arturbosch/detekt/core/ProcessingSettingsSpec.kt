package io.gitlab.arturbosch.detekt.core

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProcessingSettingsSpec {

    @Test
    fun `processingSettings_config with empty config and without default config is empty`() {
        val processingSettings = ProcessingSettings(
            ProcessingSpec { config { useDefaultConfig = false } },
            TestConfig(),
        )

        assertThat(processingSettings.config.subConfig("config").valueOrNull<Boolean>("validation")).isNull()
    }

    @Test
    fun `processingSettings_config with empty config and with default config is default config`() {
        val processingSettings = ProcessingSettings(
            ProcessingSpec { config { useDefaultConfig = true } },
            TestConfig(),
        )

        assertThat(processingSettings.config.subConfig("config").valueOrNull<Boolean>("validation")).isTrue()
    }

    @Test
    fun `processingSettings_baseConfig with empty config and without default config is empty`() {
        val processingSettings = ProcessingSettings(
            ProcessingSpec { config { useDefaultConfig = false } },
            TestConfig(),
        )

        assertThat(processingSettings.baseConfig.subConfig("config").valueOrNull<Boolean>("validation")).isNull()
    }

    @Test
    fun `processingSettings_baseConfig with empty config and with default config is empty`() {
        val processingSettings = ProcessingSettings(
            ProcessingSpec { config { useDefaultConfig = true } },
            TestConfig(),
        )

        assertThat(processingSettings.baseConfig.subConfig("config").valueOrNull<Boolean>("validation")).isNull()
    }
}
