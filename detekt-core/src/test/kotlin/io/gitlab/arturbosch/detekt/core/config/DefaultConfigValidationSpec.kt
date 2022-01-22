package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultConfigValidationSpec {

    @Nested
    inner class `default configuration is valid` {

        private val baseline = yamlConfig("default-detekt-config.yml")

        @Test
        fun `is valid comparing itself`() {
            assertThat(validateConfig(baseline, baseline)).isEmpty()
        }

        @Test
        fun `does not flag common known config sub sections`() {
            assertThat(validateConfig(yamlConfig("common_known_sections.yml"), baseline)).isEmpty()
        }
    }
}
