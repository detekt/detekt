package io.gitlab.arturbosch.detekt.api

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import io.gitlab.arturbosch.detekt.test.hasKeyValue
import io.gitlab.arturbosch.detekt.test.hasNotKey
import io.gitlab.arturbosch.detekt.test.valueOrDefault
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ConfigSpec : Spek({

    describe("load yaml config") {

        val config by memoized { yamlConfig("detekt.yml") }

        it("should create a sub config") {
            try {
                val subConfig = config.subConfig("style")
                expect(subConfig) {
                    valueOrDefault("WildcardImport", mapOf<String, Any>()) {
                        isNotEmpty()
                        getExisting("active").toBe(true)

                    }
                    valueOrDefault("NotFound", mapOf<String, Any>()).isEmpty()
                    valueOrDefault("NotFound", "").isEmpty()
                }
            } catch (ignored: Config.InvalidConfigurationError) {
                fail("Creating a sub config should work for test resources config!")
            }
        }

        it("should create a sub sub config") {
            try {
                val subConfig = config.subConfig("style")
                val subSubConfig = subConfig.subConfig("WildcardImport")
                expect(subSubConfig) {
                    //active should always have a value
                    valueOrDefault("active", false).toBe(true)
                    hasNotKey("NotFound")
                    valueOrDefault("NotFound", true).toBe(true)
                }
            } catch (ignored: Config.InvalidConfigurationError) {
                fail("Creating a sub config should work for test resources config!")
            }
        }

        it("tests wrong sub config conversion") {
            expect {
                @Suppress("UNUSED_VARIABLE")
                val ignored = config.valueOrDefault("style", "")
            }.toThrow<IllegalStateException> {
                message.toBe("Value \"{WildcardImport={active=true}, NoElseInWhenExpression={active=true}, MagicNumber={active=true, ignoreNumbers=-1,0,1,2}}\" set for config parameter \"style\" is not of required type String.")
            }
        }
    }

    describe("loading empty configurations") {

        it("empty yaml file is equivalent to empty config") {
            val config = YamlConfig.loadResource(javaClass.getResource("/empty.yml"))
            // also an empty config should return true for active
            expect(config).hasKeyValue("active", true)
        }

        it("single item without value in yaml file is valid") {
            YamlConfig.loadResource(javaClass.getResource("/oneitem.yml"))
        }
    }

    describe("meaningful error messages") {

        val config by memoized { yamlConfig("wrong-property-type.yml") }

        it("prints whole config-key path for NumberFormatException") {
            expect {
                config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("threshold", 6)
            }.toThrow<IllegalStateException> {
                message.toBe("Value \"v5.7\" set for config parameter \"RuleSet > Rule > threshold\" is not of required type Int.")
            }
        }

        it("prints whole config-key path for ClassCastException") {
            expect {
                @Suppress("UNUSED_VARIABLE")
                val bool: Int = config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("active", 1)
            }.toThrow<IllegalStateException> {
                message.toBe("Value \"[]\" set for config parameter \"RuleSet > Rule > active\" is not of required type Int.")
            }
        }
    }
})
