package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.DefaultValue.Companion.of
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val defaultConfiguration = Configuration(
    name = "name",
    description = "description",
    defaultValue = of(""),
    defaultAndroidValue = null,
    deprecated = null
)

class ConfigurationSpec {

    @Nested
    inner class `default value to list conversion` {
        @Nested
        inner class `empty default value` {
            private val subject = defaultConfiguration.copy(defaultValue = of(""))

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
            private val subject = defaultConfiguration.copy(defaultValue = of("abc"))

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
            private val subject = defaultConfiguration.copy(defaultValue = of(emptyList()))

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
            private val subject = defaultConfiguration.copy(defaultValue = of(listOf("a", "b")))

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
