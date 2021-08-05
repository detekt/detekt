package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.concurrent.atomic.AtomicInteger

class ConfigPropertySpec : Spek({

    describe("Config property delegate") {
        context("simple property") {
            context("String property") {
                val configValue = "value"
                val defaultValue = "default"
                val subject by memoized {
                    object : TestConfigAware("present" to configValue) {
                        val present: String by config(defaultValue)
                        val notPresent: String by config(defaultValue)
                    }
                }
                it("uses the value provided in config if present") {
                    assertThat(subject.present).isEqualTo(configValue)
                }
                it("uses the default value if not present") {
                    assertThat(subject.notPresent).isEqualTo(defaultValue)
                }
            }
            context("Int property") {
                val configValue = 99
                val defaultValue = -1

                context("defined as number") {
                    val subject by memoized {
                        object : TestConfigAware("present" to configValue) {
                            val present: Int by config(defaultValue)
                            val notPresent: Int by config(defaultValue)
                        }
                    }
                    it("uses the value provided in config if present") {
                        assertThat(subject.present).isEqualTo(configValue)
                    }
                    it("uses the default value if not present") {
                        assertThat(subject.notPresent).isEqualTo(defaultValue)
                    }
                }
                context("defined as string") {
                    val subject by memoized {
                        object : TestConfigAware("present" to "$configValue") {
                            val present: Int by config(defaultValue)
                        }
                    }
                    it("uses the value provided in config if present") {
                        assertThat(subject.present).isEqualTo(configValue)
                    }
                }
            }
            context("Boolean property") {
                val configValue by memoized { false }
                val defaultValue by memoized { true }

                context("defined as Boolean") {
                    val subject by memoized {
                        object : TestConfigAware("present" to configValue) {
                            val present: Boolean by config(defaultValue)
                            val notPresent: Boolean by config(defaultValue)
                        }
                    }
                    it("uses the value provided in config if present") {
                        assertThat(subject.present).isEqualTo(configValue)
                    }
                    it("uses the default value if not present") {
                        assertThat(subject.notPresent).isEqualTo(defaultValue)
                    }
                }
                context("defined as string") {
                    val subject by memoized {
                        object : TestConfigAware("present" to "$configValue") {
                            val present: Boolean by config(defaultValue)
                        }
                    }
                    it("uses the value provided in config if present") {
                        assertThat(subject.present).isEqualTo(configValue)
                    }
                }
            }
            context("List property") {
                val defaultValue by memoized { listOf("x") }
                context("defined as list") {
                    val subject by memoized {
                        object : TestConfigAware("present" to "a,b,c") {
                            val present: List<String> by config(defaultValue)
                            val notPresent: List<String> by config(defaultValue)
                        }
                    }
                    it("uses the value provided in config if present") {
                        assertThat(subject.present).isEqualTo(listOf("a", "b", "c"))
                    }
                    it("uses the default value if not present") {
                        assertThat(subject.notPresent).isEqualTo(defaultValue)
                    }
                }
                context("defined as comma separated string") {
                    val subject by memoized {
                        object : TestConfigAware("present" to "a,b,c") {
                            val present: List<String> by config(defaultValue)
                            val notPresent: List<String> by config(defaultValue)
                        }
                    }
                    it("uses the value provided in config if present") {
                        assertThat(subject.present).isEqualTo(listOf("a", "b", "c"))
                    }
                    it("uses the default value if not present") {
                        assertThat(subject.notPresent).isEqualTo(defaultValue)
                    }
                }
            }
        }
        context("invalid type") {
            context("Long") {
                val defaultValue by memoized { 1L }
                val subject by memoized {
                    object : TestConfigAware() {
                        val prop: Long by config(defaultValue)
                    }
                }
                it("throws") {
                    assertThatThrownBy { subject.prop }
                        .isInstanceOf(IllegalStateException::class.java)
                        .hasMessageContaining("is not supported")
                }
            }
            context("Regex") {
                val defaultValue by memoized { Regex("a") }
                val subject by memoized {
                    object : TestConfigAware() {
                        val prop: Regex by config(defaultValue)
                    }
                }
                it("throws") {
                    assertThatThrownBy { subject.prop }
                        .isInstanceOf(IllegalStateException::class.java)
                        .hasMessageContaining("is not supported")
                }
            }
            context("Set") {
                val defaultValue by memoized { setOf("a") }
                val subject by memoized {
                    object : TestConfigAware() {
                        val prop: Set<String> by config(defaultValue)
                    }
                }
                it("throws") {
                    assertThatThrownBy { subject.prop }
                        .isInstanceOf(IllegalStateException::class.java)
                        .hasMessageContaining("is not supported")
                }
            }
            context("List of Int") {
                val defaultValue by memoized { listOf(1) }
                val subject by memoized {
                    object : TestConfigAware() {
                        val prop: List<Int> by config(defaultValue)
                    }
                }
                it("throws") {
                    assertThatThrownBy { subject.prop }
                        .isInstanceOf(IllegalStateException::class.java)
                        .hasMessageContaining("lists of strings are supported")
                }
            }
        }

        context("transform") {
            context("primitive") {
                context("String property is transformed to regex") {
                    val defaultValue = ".*"
                    val configValue = "[a-z]+"
                    val subject by memoized {
                        object : TestConfigAware("present" to configValue) {
                            val present: Regex by config(defaultValue) { it.toRegex() }
                            val notPresent: Regex by config(defaultValue) { it.toRegex() }
                        }
                    }
                    it("applies the mapping function to the configured value") {
                        assertThat(subject.present.matches("abc")).isTrue
                        assertThat(subject.present.matches("123")).isFalse()
                    }
                    it("applies the mapping function to the default") {
                        assertThat(subject.notPresent.matches("abc")).isTrue
                        assertThat(subject.notPresent.matches("123")).isTrue
                    }
                }
                context("Int property is transformed to String") {
                    val configValue = 99
                    val defaultValue = -1
                    val subject by memoized {
                        object : TestConfigAware("present" to configValue) {
                            val present: String by config(defaultValue) { it.toString() }
                            val notPresent: String by config(defaultValue) { it.toString() }
                        }
                    }
                    it("applies the mapping function to the configured value") {
                        assertThat(subject.present).isEqualTo("$configValue")
                    }
                    it("applies the mapping function to the default") {
                        assertThat(subject.notPresent).isEqualTo("$defaultValue")
                    }
                }
                context("Boolean property is transformed to String") {
                    val configValue by memoized { true }
                    val defaultValue by memoized { false }
                    val subject by memoized {
                        object : TestConfigAware("present" to configValue) {
                            val present: String by config(defaultValue) { it.toString() }
                            val notPresent: String by config(defaultValue) { it.toString() }
                        }
                    }
                    it("applies the mapping function to the configured value") {
                        assertThat(subject.present).isEqualTo("$configValue")
                    }
                    it("applies the mapping function to the default") {
                        assertThat(subject.notPresent).isEqualTo("$defaultValue")
                    }
                }
                context("Boolean property is transformed to String with function reference") {
                    val defaultValue by memoized { false }
                    val subject by memoized {
                        object : TestConfigAware() {
                            val prop1: String by config(defaultValue, Boolean::toString)
                            val prop2: String by config(transformer = Boolean::toString, defaultValue = defaultValue)
                        }
                    }
                    it("transforms properties") {
                        assertThat(subject.prop1).isEqualTo("$defaultValue")
                        assertThat(subject.prop2).isEqualTo("$defaultValue")
                    }
                }
            }
            context("list of strings") {
                val defaultValue by memoized { listOf("99") }
                val subject by memoized {
                    object : TestConfigAware("present" to "1,2,3") {
                        val present: Int by config(defaultValue) { it.sumOf(String::toInt) }
                        val notPresent: Int by config(defaultValue) { it.sumOf(String::toInt) }
                    }
                }
                it("applies transformer to list configured") {
                    assertThat(subject.present).isEqualTo(6)
                }
                it("applies transformer to default list") {
                    assertThat(subject.notPresent).isEqualTo(99)
                }
            }
            context("empty list of strings") {
                val subject by memoized {
                    object : TestConfigAware() {
                        val defaultValue: List<String> = emptyList()
                        val prop1: List<Int> by config(defaultValue) { it.map(String::toInt) }
                        val prop2: List<Int> by config(emptyList<String>()) { it.map(String::toInt) }
                    }
                }
                it("can be defined as variable") {
                    assertThat(subject.prop1).isEmpty()
                }
                it("can be defined using listOf<String>()") {
                    assertThat(subject.prop2).isEmpty()
                }
            }
            context("memoization") {
                val subject by memoized {
                    object : TestConfigAware() {
                        val counter = AtomicInteger(0)
                        val prop: String by config(1) {
                            counter.getAndIncrement()
                            it.toString()
                        }

                        fun useProperty(): String {
                            return "something with $prop"
                        }
                    }
                }
                it("transformer is called only once") {
                    repeat(5) {
                        assertThat(subject.useProperty()).isEqualTo("something with 1")
                    }
                    assertThat(subject.counter.get()).isEqualTo(1)
                }
            }
        }
        context("configWithFallback") {
            context("primitive") {
                val configValue = 99
                val defaultValue = 0
                val fallbackValue = -1
                val subject by memoized {
                    object : TestConfigAware("present" to "$configValue", "fallback" to fallbackValue) {
                        val present: Int by configWithFallback("fallback", defaultValue)
                        val notPresentWithFallback: Int by configWithFallback("fallback", defaultValue)
                        val notPresentFallbackMissing: Int by configWithFallback("missing", defaultValue)
                    }
                }
                it("uses the value provided in config if present") {
                    assertThat(subject.present).isEqualTo(configValue)
                }
                it("uses the value from fallback property if value is missing and fallback exists") {
                    assertThat(subject.notPresentWithFallback).isEqualTo(fallbackValue)
                }
                it("uses the default value if not present") {
                    assertThat(subject.notPresentFallbackMissing).isEqualTo(defaultValue)
                }
            }
            context("with transformation") {
                val configValue = 99
                val defaultValue = 0
                val fallbackValue = -1
                val subject by memoized {
                    object : TestConfigAware("present" to configValue, "fallback" to fallbackValue) {
                        val present: String by configWithFallback("fallback", defaultValue) { v ->
                            v.toString()
                        }
                        val notPresentWithFallback: String by configWithFallback("fallback", defaultValue) { v ->
                            v.toString()
                        }
                        val notPresentFallbackMissing: String by configWithFallback("missing", defaultValue) { v ->
                            v.toString()
                        }
                    }
                }
                it("uses the value provided in config if present") {
                    assertThat(subject.present).isEqualTo("$configValue")
                }
                it("uses the value from fallback property if value is missing and fallback exists") {
                    assertThat(subject.notPresentWithFallback).isEqualTo("$fallbackValue")
                }
                it("uses the default value if not present") {
                    assertThat(subject.notPresentFallbackMissing).isEqualTo("$defaultValue")
                }
            }
        }
    }
})

private open class TestConfigAware(private vararg val data: Pair<String, Any>) : ConfigAware {
    override val ruleId: RuleId
        get() = "test"
    override val ruleSetConfig: Config
        get() = TestConfig(data.toMap())
}
