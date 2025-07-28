package dev.detekt.gradle.plugin.invoke

import dev.detekt.gradle.plugin.extensions.FailOnSeverity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class FailOnSeverityArgumentSpec {
    @ParameterizedTest
    @EnumSource(FailOnSeverity::class)
    fun `use severity if ignoreFailures is false`(severity: FailOnSeverity) {
        val subject = FailOnSeverityArgument(ignoreFailures = false, minSeverity = severity)

        val actual = subject.toArgument()

        assertThat(actual).hasSize(2)
        assertThat(actual.first()).isEqualTo("--fail-on-severity")
        assertThat(actual.last()).isEqualToIgnoringCase(severity.name)
    }

    @ParameterizedTest
    @EnumSource(FailOnSeverity::class)
    fun `use Never if ignoreFailures is true`(severity: FailOnSeverity) {
        val subject = FailOnSeverityArgument(ignoreFailures = true, minSeverity = severity)

        val actual = subject.toArgument()

        assertThat(actual).hasSize(2)
        assertThat(actual.first()).isEqualTo("--fail-on-severity")
        assertThat(actual.last()).isEqualToIgnoringCase(FailOnSeverity.Never.name)
    }
}
