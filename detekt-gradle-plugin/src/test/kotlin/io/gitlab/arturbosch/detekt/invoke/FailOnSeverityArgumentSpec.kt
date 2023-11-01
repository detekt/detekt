package io.gitlab.arturbosch.detekt.invoke

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FailOnSeverityArgumentSpec {
    @ParameterizedTest
    @ValueSource(strings = ["NEVER", "never", "INFO", "info", "WARNING", "warning", "ERROR", "error"])
    fun `convert valid options`(validOption: String) {
        val subject = FailOnSeverityArgument(ignoreFailures = false, minSeverity = validOption)

        val actual = subject.toArgument()

        assertThat(actual).hasSize(2)
        assertThat(actual.first()).isEqualTo("--fail-on-severity")
        assertThat(actual.last()).isEqualToIgnoringCase(validOption)
    }

    @Test
    fun `fail on unknown severity value`() {
        val subject = FailOnSeverityArgument(ignoreFailures = false, minSeverity = "unKnown")

        assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            subject.toArgument()
        }.withMessageContaining("is not a valid option for failOnSeverity")
    }
}
