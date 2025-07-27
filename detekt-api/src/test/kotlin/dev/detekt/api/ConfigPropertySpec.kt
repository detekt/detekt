package dev.detekt.api

import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class ConfigPropertySpec {

    @Nested
    inner class `simple property` {
        @Nested
        inner class `String property` {
            private val configValue = "value"
            private val defaultValue = "default"
            private val subject = object : TestRule("present" to configValue) {
                val present: String by config(defaultValue)
                val notPresent: String by config(defaultValue)
            }

            @Test
            fun `uses the value provided in config if present`() {
                assertThat(subject.present).isEqualTo(configValue)
            }

            @Test
            fun `uses the default value if not present`() {
                assertThat(subject.notPresent).isEqualTo(defaultValue)
            }
        }

        @Nested
        inner class `Int property` {
            private val configValue = 99
            private val defaultValue = -1

            @Nested
            inner class `defined as number` {
                private val subject = object : TestRule("present" to configValue) {
                    val present: Int by config(defaultValue)
                    val notPresent: Int by config(defaultValue)
                }

                @Test
                fun `uses the value provided in config if present`() {
                    assertThat(subject.present).isEqualTo(configValue)
                }

                @Test
                fun `uses the default value if not present`() {
                    assertThat(subject.notPresent).isEqualTo(defaultValue)
                }
            }

            @Nested
            inner class `defined as string` {
                private val subject = object : TestRule("present" to "$configValue") {
                    val present: Int by config(defaultValue)
                }

                @Test
                fun `uses the value provided in config if present`() {
                    assertThat(subject.present).isEqualTo(configValue)
                }
            }

            @Nested
            inner class `ValuesWithReason property` {
                private val defaultValue = valuesWithReason("aValue" to "aReason")

                @Nested
                inner class `value defined as list` {
                    private val subject = object : TestRule("present" to listOf("a", "b", "c")) {
                        val present: ValuesWithReason by config(defaultValue)
                        val notPresent: ValuesWithReason by config(defaultValue)
                    }

                    @Test
                    fun `uses the value provided in config if present`() {
                        assertThat(subject.present)
                            .extracting(ValueWithReason::value, ValueWithReason::reason)
                            .containsExactly(tuple("a", null), tuple("b", null), tuple("c", null))
                    }

                    @Test
                    fun `uses the default value if not present`() {
                        assertThat(subject.notPresent).isEqualTo(defaultValue)
                    }
                }

                @Nested
                inner class `value defined as list of maps` {
                    private val subject = object : TestRule(
                        "present" to listOf(
                            mapOf("value" to "a", "reason" to "reasonA"),
                            mapOf("value" to "b", "reason" to null),
                            mapOf("value" to "c"),
                        )
                    ) {
                        val present: ValuesWithReason by config(defaultValue)
                        val notPresent: ValuesWithReason by config(defaultValue)
                    }

                    @Test
                    fun `uses the value provided in config if present`() {
                        assertThat(subject.present)
                            .extracting(ValueWithReason::value, ValueWithReason::reason)
                            .containsExactly(
                                tuple("a", "reasonA"),
                                tuple("b", null),
                                tuple("c", null)
                            )
                    }

                    @Test
                    fun `uses the default value if not present`() {
                        assertThat(subject.notPresent).isEqualTo(defaultValue)
                    }
                }

                @Nested
                inner class `value defined as list of string and map` {
                    private val subject = object : TestRule(
                        "present" to listOf(
                            "a",
                            mapOf("value" to "b", "reason" to "reasonB"),
                            mapOf("value" to "c", "reason" to null),
                            mapOf("value" to "d"),
                        )
                    ) {
                        val present: ValuesWithReason by config(defaultValue)
                    }

                    @Test
                    fun `uses the value provided in config if present`() {
                        assertThat(subject.present)
                            .hasSize(4)
                            .extracting(ValueWithReason::value, ValueWithReason::reason)
                            .containsExactly(
                                tuple("a", null),
                                tuple("b", "reasonB"),
                                tuple("c", null),
                                tuple("d", null),
                            )
                    }
                }

                @Nested
                inner class `value defined as list of maps with invalid data` {

                    @Test
                    fun `value missing`() {
                        assertThatThrownBy {
                            object : TestRule(
                                "present" to listOf(
                                    mapOf("reason" to "reason")
                                )
                            ) {
                                val present: ValuesWithReason by config(defaultValue)
                            }.present
                        }.isInstanceOf(Config.InvalidConfigurationError::class.java)
                    }

                    @Test
                    fun `value with an invalid type`() {
                        assertThatThrownBy {
                            object : TestRule(
                                "present" to listOf(
                                    mapOf("value" to 42, "reason" to "reason")
                                )
                            ) {
                                val present: ValuesWithReason by config(defaultValue)
                            }.present
                        }.isInstanceOf(Config.InvalidConfigurationError::class.java)
                    }

                    @Test
                    fun `reason with an invalid type`() {
                        assertThatThrownBy {
                            object : TestRule(
                                "present" to listOf(
                                    mapOf("value" to "a", "reason" to 42)
                                )
                            ) {
                                val present: ValuesWithReason by config(defaultValue)
                            }.present
                        }.isInstanceOf(Config.InvalidConfigurationError::class.java)
                    }
                }
            }
        }

        @Nested
        inner class `Boolean property` {
            private val configValue = false
            private val defaultValue = true

            @Nested
            inner class `defined as Boolean` {
                private val subject = object : TestRule("present" to configValue) {
                    val present: Boolean by config(defaultValue)
                    val notPresent: Boolean by config(defaultValue)
                }

                @Test
                fun `uses the value provided in config if present`() {
                    assertThat(subject.present).isEqualTo(configValue)
                }

                @Test
                fun `uses the default value if not present`() {
                    assertThat(subject.notPresent).isEqualTo(defaultValue)
                }
            }

            @Nested
            inner class `defined as string` {
                private val subject = object : TestRule("present" to "$configValue") {
                    val present: Boolean by config(defaultValue)
                }

                @Test
                fun `uses the value provided in config if present`() {
                    assertThat(subject.present).isEqualTo(configValue)
                }
            }
        }

        @Nested
        inner class `List property` {
            private val defaultValue = listOf("x")

            @Nested
            inner class `defined as list` {
                private val subject = object : TestRule("present" to listOf("a", "b", "c")) {
                    val present: List<String> by config(defaultValue)
                    val notPresent: List<String> by config(defaultValue)
                }

                @Test
                fun `uses the value provided in config if present`() {
                    assertThat(subject.present).isEqualTo(listOf("a", "b", "c"))
                }

                @Test
                fun `uses the default value if not present`() {
                    assertThat(subject.notPresent).isEqualTo(defaultValue)
                }
            }

            @Nested
            inner class `defined as comma separated string` {
                private val subject = object : TestRule("present" to listOf("a", "b", "c")) {
                    val present: List<String> by config(defaultValue)
                    val notPresent: List<String> by config(defaultValue)
                }

                @Test
                fun `uses the value provided in config if present`() {
                    assertThat(subject.present).isEqualTo(listOf("a", "b", "c"))
                }

                @Test
                fun `uses the default value if not present`() {
                    assertThat(subject.notPresent).isEqualTo(defaultValue)
                }
            }
        }
    }

    @Nested
    inner class `invalid type` {
        @Nested
        inner class `Long property` {
            private val defaultValue = 1L
            private val subject = object : TestRule() {
                val prop: Long by config(defaultValue)
            }

            @Test
            fun throws() {
                assertThatThrownBy { subject.prop }
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("is not supported")
            }
        }

        @Nested
        inner class `Regex property` {
            private val defaultValue = Regex("a")
            private val subject = object : TestRule() {
                val prop: Regex by config(defaultValue)
            }

            @Test
            fun throws() {
                assertThatThrownBy { subject.prop }
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("is not supported")
            }
        }

        @Nested
        inner class `Set property` {
            private val defaultValue = setOf("a")
            private val subject = object : TestRule() {
                val prop: Set<String> by config(defaultValue)
            }

            @Test
            fun throws() {
                assertThatThrownBy { subject.prop }
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("is not supported")
            }
        }

        @Nested
        inner class `List of Int` {
            private val defaultValue = listOf(1)
            private val subject = object : TestRule() {
                val prop: List<Int> by config(defaultValue)
            }

            @Test
            fun throws() {
                assertThatThrownBy { subject.prop }
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("lists of strings are supported")
            }
        }
    }

    @Nested
    inner class Transform {
        @Nested
        inner class Primitive {
            @Nested
            inner class `String property is transformed to regex` {
                private val defaultValue = ".*"
                private val configValue = "[a-z]+"
                private val subject = object : TestRule("present" to configValue) {
                    val present: Regex by config(defaultValue) { it.toRegex() }
                    val notPresent: Regex by config(defaultValue) { it.toRegex() }
                }

                @Test
                fun `applies the mapping function to the configured value`() {
                    assertThat(subject.present.matches("abc")).isTrue
                    assertThat(subject.present.matches("123")).isFalse()
                }

                @Test
                fun `applies the mapping function to the default`() {
                    assertThat(subject.notPresent.matches("abc")).isTrue
                    assertThat(subject.notPresent.matches("123")).isTrue
                }
            }

            @Nested
            inner class `Int property is transformed to String` {
                private val configValue = 99
                private val defaultValue = -1
                private val subject = object : TestRule("present" to configValue) {
                    val present: String by config(defaultValue) { it.toString() }
                    val notPresent: String by config(defaultValue) { it.toString() }
                }

                @Test
                fun `applies the mapping function to the configured value`() {
                    assertThat(subject.present).isEqualTo("$configValue")
                }

                @Test
                fun `applies the mapping function to the default`() {
                    assertThat(subject.notPresent).isEqualTo("$defaultValue")
                }
            }

            @Nested
            inner class `Boolean property is transformed to String` {
                private val configValue = true
                private val defaultValue = false
                private val subject = object : TestRule("present" to configValue) {
                    val present: String by config(defaultValue) { it.toString() }
                    val notPresent: String by config(defaultValue) { it.toString() }
                }

                @Test
                fun `applies the mapping function to the configured value`() {
                    assertThat(subject.present).isEqualTo("$configValue")
                }

                @Test
                fun `applies the mapping function to the default`() {
                    assertThat(subject.notPresent).isEqualTo("$defaultValue")
                }
            }

            @Nested
            inner class `Boolean property is transformed to String with function reference` {
                private val defaultValue = false
                private val subject = object : TestRule() {
                    val prop1: String by config(defaultValue, Boolean::toString)
                    val prop2: String by config(transformer = Boolean::toString, defaultValue = defaultValue)
                }

                @Test
                fun `transforms properties`() {
                    assertThat(subject.prop1).isEqualTo("$defaultValue")
                    assertThat(subject.prop2).isEqualTo("$defaultValue")
                }
            }
        }

        @Nested
        inner class `list of strings` {
            private val defaultValue = listOf("99")
            private val subject = object : TestRule("present" to listOf("1", "2", "3")) {
                val present: Int by config(defaultValue) { it.sumOf(String::toInt) }
                val notPresent: Int by config(defaultValue) { it.sumOf(String::toInt) }
            }

            @Test
            fun `applies transformer to list configured`() {
                assertThat(subject.present).isEqualTo(6)
            }

            @Test
            fun `applies transformer to default list`() {
                assertThat(subject.notPresent).isEqualTo(99)
            }
        }

        @Nested
        inner class `empty list of strings` {
            private val subject = object : TestRule() {
                val defaultValue: List<String> = emptyList()
                val prop1: List<Int> by config(defaultValue) { it.map(String::toInt) }
                val prop2: List<Int> by config(emptyList<String>()) { it.map(String::toInt) }
            }

            @Test
            fun `can be defined as variable`() {
                assertThat(subject.prop1).isEmpty()
            }

            @Test
            fun `can be defined using listOf`() {
                assertThat(subject.prop2).isEmpty()
            }
        }

        @Nested
        inner class Memoization {
            private val subject = object : TestRule() {
                val counter = AtomicInteger(0)
                val prop: String by config(1) {
                    counter.getAndIncrement()
                    it.toString()
                }

                fun useProperty(): String = "something with $prop"
            }

            @Test
            fun `transformer is called only once`() {
                repeat(5) {
                    assertThat(subject.useProperty()).isEqualTo("something with 1")
                }
                assertThat(subject.counter).hasValue(1)
            }
        }
    }

    @Nested
    inner class ConfigWithFallback {
        @Nested
        inner class Primitive {
            private val configValue = 99
            private val defaultValue = 0
            private val fallbackValue = -1
            private val subject =
                object : TestRule("present" to "$configValue", "fallback" to fallbackValue) {
                    private val fallback: Int by config(42)
                    private val missing: Int by config(42)
                    val present: Int by configWithFallback(::fallback, defaultValue)
                    val notPresentWithFallback: Int by configWithFallback(::fallback, defaultValue)
                    val notPresentFallbackMissing: Int by configWithFallback(::missing, defaultValue)
                }

            @Test
            fun `uses the value provided in config if present`() {
                assertThat(subject.present).isEqualTo(configValue)
            }

            @Test
            fun `uses the value from fallback property if value is missing and fallback exists`() {
                assertThat(subject.notPresentWithFallback).isEqualTo(fallbackValue)
            }

            @Test
            fun `uses the default value if not present`() {
                assertThat(subject.notPresentFallbackMissing).isEqualTo(defaultValue)
            }
        }

        @Nested
        inner class `with transformation` {
            private val configValue = 99
            private val defaultValue = 0
            private val fallbackValue = -1
            private val fallbackOffset = 10
            private val subject = object : TestRule("present" to configValue, "fallback" to fallbackValue) {
                private val fallback: String by config(42) { (it + fallbackOffset).toString() }
                private val missing: String by config(42) { (it + fallbackOffset).toString() }
                val present: String by configWithFallback(::fallback, defaultValue) { v ->
                    v.toString()
                }
                val notPresentWithFallback: String by configWithFallback(::fallback, defaultValue) { v ->
                    v.toString()
                }
                val notPresentFallbackMissing: String by configWithFallback(::missing, defaultValue) { v ->
                    v.toString()
                }
            }

            @Test
            fun `uses the value provided in config if present`() {
                assertThat(subject.present).isEqualTo("$configValue")
            }

            @Test
            fun `transforms the value from fallback property if value is missing and fallback exists`() {
                assertThat(subject.notPresentWithFallback).isEqualTo("${fallbackValue + fallbackOffset}")
            }

            @Test
            fun `uses the default value if not present`() {
                assertThat(subject.notPresentFallbackMissing).isEqualTo("$defaultValue")
            }
        }
    }
}

open class TestRule(vararg data: Pair<String, Any>) : Rule(TestConfig(*data), "description")
