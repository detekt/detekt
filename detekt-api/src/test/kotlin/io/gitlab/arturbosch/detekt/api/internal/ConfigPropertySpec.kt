package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ConfigPropertySpec : Spek({

    describe("Config property delegate") {
        context("string property") {
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
        context("Int property defined as string") {
            val configValue = 99
            val subject by memoized {
                object : TestConfigAware("present" to "$configValue") {
                    val present: Int by config(-1)
                }
            }
            it("uses the value provided in config if present") {
                assertThat(subject.present).isEqualTo(configValue)
            }
        }
        context("Boolean property") {
            val configValue = false
            val defaultValue = true
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
        context("Boolean property defined as string") {
            val configValue = false
            val defaultValue = true
            val subject by memoized {
                object : TestConfigAware("present" to "$configValue") {
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
        context("String list property") {
            val defaultValue by memoized { listOf("x") }
            val subject by memoized {
                object : TestConfigAware("present" to listOf("a", "b", "c")) {
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
        context("String list property defined as comma separated string") {
            val defaultValue by memoized { listOf("x") }
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
        context("Int property with fallback") {
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
        context("Invalid property type") {
            val defaultRegex = Regex("a-z")
            val defaultList = listOf(1)
            val subject by memoized {
                object : TestConfigAware() {
                    val regexProp: Regex by config(defaultRegex)
                    val listProp: List<Int> by config(defaultList)
                }
            }
            it("fails when invalid regex property is accessed") {
                assertThatThrownBy { subject.regexProp }
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("kotlin.text.Regex is not supported")
            }
            it("fails when invalid list property is accessed") {
                assertThatThrownBy { subject.listProp }
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Only lists of strings are supported")
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
