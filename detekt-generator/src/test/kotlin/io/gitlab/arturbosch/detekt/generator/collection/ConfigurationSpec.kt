package io.gitlab.arturbosch.detekt.generator.collection

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private val defaultConfiguration = Configuration(
    name = "name",
    description = "description",
    defaultValue = "",
    deprecated = null
)

object ConfigurationSpec : Spek({

    describe("default value to list conversion") {
        describe("empty default value") {
            val subject by memoized { defaultConfiguration.copy(defaultValue = "") }

            it("identifies default as not a list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            it("fails when attempting conversion") {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }
        describe("non list default value") {
            val subject by memoized { defaultConfiguration.copy(defaultValue = "abc") }

            it("identifies default as not a list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            it("fails when attempting conversion") {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }
        describe("empty list default value") {
            val subject by memoized { defaultConfiguration.copy(defaultValue = "[ ]") }

            it("identifies default as not a non empty list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isFalse()
            }

            it("fails when attempting conversion") {
                assertThatIllegalStateException().isThrownBy { subject.getDefaultValueAsList() }
            }
        }
        describe("bracket list default value") {
            val subject by memoized { defaultConfiguration.copy(defaultValue = "[ 'a', 'b' ]") }

            it("identifies default as a non empty list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isTrue()
            }

            it("converts to a list") {
                assertThat(subject.getDefaultValueAsList()).isEqualTo(listOf("a", "b"))
            }
        }
        describe("yaml list default value (allowed in rule set provider kdoc)") {
            val defaultValue = """- a
               - b
               - c"""
            val subject by memoized { defaultConfiguration.copy(defaultValue = defaultValue) }

            it("identifies default as a non empty list") {
                assertThat(subject.isDefaultValueNonEmptyList()).isTrue()
            }

            it("converts to a list") {
                assertThat(subject.getDefaultValueAsList()).isEqualTo(listOf("a", "b", "c"))
            }
        }
    }
})
