package io.gitlab.arturbosch.detekt.generator.collection

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val defaultConfiguration = Configuration(
    name = "name",
    description = "description",
    defaultValue = "",
    defaultAndroidValue = null,
    deprecated = null
)

class ConfigurationTest {

    @Nested
    inner class `default value to list conversion` {
        @Nested
        inner class `empty default value` {
            val subject = defaultConfiguration.copy(defaultValue = "")

            @Test
            fun `identifies default as not a list`() {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            @Test
            fun `fails when attempting conversion`() {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }

        @Nested
        inner class `non list default value` {
            val subject = defaultConfiguration.copy(defaultValue = "abc")

            @Test
            fun `identifies default as not a list`() {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            @Test
            fun `fails when attempting conversion`() {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }

        @Nested
        inner class `empty list default value` {
            val subject = defaultConfiguration.copy(defaultValue = "[ ]")

            @Test
            fun `identifies default as not a non empty list`() {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            @Test
            fun `fails when attempting conversion`() {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }

        @Nested
        inner class `bracket list default value` {
            val subject = defaultConfiguration.copy(defaultValue = "[ 'a', 'b' ]")

            @Test
            fun `identifies default as a non empty list`() {
                assertThat(subject.isDefaultValueNonEmptyList()).isTrue()
            }

            @Test
            fun `converts to a list`() {
                assertThat(subject.getDefaultValueAsList()).isEqualTo(listOf("a", "b"))
            }
        }
    }
}
