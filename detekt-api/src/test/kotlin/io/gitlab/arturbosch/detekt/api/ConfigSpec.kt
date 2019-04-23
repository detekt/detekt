package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class ConfigSpec : Spek({

    describe("load yaml config") {

        val config by memoized { yamlConfig("detekt.yml") }

        it("should create a sub config") {
            try {
                val subConfig = config.subConfig("style")
                assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())).isNotEmpty
                assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())["active"].toString()).isEqualTo("true")
                assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())["active"] as Boolean).isTrue()
                assertThat(subConfig.valueOrDefault("NotFound", mapOf<String, Any>())).isEmpty()
                assertThat(subConfig.valueOrDefault("NotFound", "")).isEmpty()
            } catch (ignored: Config.InvalidConfigurationError) {
                fail("Creating a sub config should work for test resources config!")
            }
        }

        it("should create a sub sub config") {
            try {
                val subConfig = config.subConfig("style")
                val subSubConfig = subConfig.subConfig("WildcardImport")
                assertThat(subSubConfig.valueOrDefault("active", false)).isTrue()
                assertThat(subSubConfig.valueOrDefault("NotFound", true)).isTrue()
            } catch (ignored: Config.InvalidConfigurationError) {
                fail("Creating a sub config should work for test resources config!")
            }
        }

        it("tests wrong sub config conversion") {
            assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy {
                @Suppress("UNUSED_VARIABLE")
                val ignored = config.valueOrDefault("style", "")
            }.withMessage("Value \"{WildcardImport={active=true}, NoElseInWhenExpression={active=true}, MagicNumber={active=true, ignoreNumbers=-1,0,1,2}}\" set for config parameter \"style\" is not of required type String.")
        }
    }

    describe("loading empty configurations") {

        it("empty yaml file is equivalent to empty config") {
            YamlConfig.loadResource(javaClass.getResource("/empty.yml"))
        }

        it("single item in yaml file is valid") {
            YamlConfig.loadResource(javaClass.getResource("/oneitem.yml"))
        }
    }

    describe("meaningful error messages") {

        val config by memoized { yamlConfig("wrong-property-type.yml") }

        it("prints whole config-key path for NumberFormatException") {
            assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy {
                config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("threshold", 6)
            }.withMessage("Value \"v5.7\" set for config parameter \"RuleSet > Rule > threshold\" is not of required type Int.")
        }

        it("prints whole config-key path for ClassCastException") {
            assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy {
                @Suppress("UNUSED_VARIABLE")
                val bool: Int = config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("active", 1)
            }.withMessage("Value \"[]\" set for config parameter \"RuleSet > Rule > active\" is not of required type Int.")
        }
    }
})
