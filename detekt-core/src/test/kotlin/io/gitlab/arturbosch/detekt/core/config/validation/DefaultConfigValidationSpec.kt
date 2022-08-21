package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.internal.CommaSeparatedPattern
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultConfigValidationSpec {

    private val baseline = yamlConfig("default-detekt-config.yml")
    private val defaultExcludePatterns = CommaSeparatedPattern(DEFAULT_PROPERTY_EXCLUDES).mapToRegex()

    @Test
    fun `is valid comparing itself`() {
        val actual = validateConfig(baseline, baseline, defaultExcludePatterns)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not flag common known config sub sections`() {
        val actual = validateConfig(yamlConfig("common_known_sections.yml"), baseline, defaultExcludePatterns)
        assertThat(actual).isEmpty()
    }
}
