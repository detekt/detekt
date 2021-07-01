package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.DefaultValue.Companion.of
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private val defaultConfiguration = Configuration(
    name = "name",
    description = "description",
    defaultValue = of(""),
    defaultAndroidValue = null,
    deprecated = null
)

object ConfigurationSpec : Spek({

    describe("default value to list conversion") {
        describe("empty default value") {
            val subject by memoized { defaultConfiguration.copy(defaultValue = of("")) }

            it("identifies default as not a list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            it("fails when attempting conversion") {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }
        describe("non list default value") {
            val subject by memoized { defaultConfiguration.copy(defaultValue = of("abc")) }

            it("identifies default as not a list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            it("fails when attempting conversion") {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }
        describe("empty list default value") {
            val subject by memoized { defaultConfiguration.copy(defaultValue = of(emptyList())) }

            it("identifies default as not a non empty list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            it("fails when attempting conversion") {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }
        describe("bracket list default value") {
            val subject by memoized { defaultConfiguration.copy(defaultValue = of(listOf("a", "b"))) }

            it("identifies default as a non empty list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isTrue()
            }

            it("converts to a list") {
                assertThat(subject.getDefaultValueAsList()).isEqualTo(listOf("a", "b"))
            }
        }
    }
})
