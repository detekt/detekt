package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
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
        context("String list property") {
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
                    val present: Int by config("fallback", defaultValue)
                    val notPresentWithFallback: Int by config("fallback", defaultValue)
                    val notPresentFallbackMissing: Int by config("missing", defaultValue)
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
    }
})

private open class TestConfigAware(private vararg val data: Pair<String, Any>) : ConfigAware {
    override val ruleId: RuleId
        get() = "test"
    override val ruleSetConfig: Config
        get() = TestConfig(data.toMap())
}
