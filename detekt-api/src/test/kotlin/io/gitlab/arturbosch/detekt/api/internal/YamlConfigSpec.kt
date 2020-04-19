package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.resource
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

@Suppress("DEPRECATION")
class YamlConfigSpec : Spek({

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
            assertThatIllegalStateException().isThrownBy {
                @Suppress("UNUSED_VARIABLE")
                val ignored = config.valueOrDefault("style", "")
            }.withMessage("Value \"{WildcardImport={active=true}, NoElseInWhenExpression={active=true}, MagicNumber={active=true, ignoreNumbers=[-1, 0, 1, 2]}}\" set for config parameter \"style\" is not of required type String.")
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

        it("only accepts true and false boolean values") {
            assertThatIllegalStateException()
                .isThrownBy { config.valueOrDefault("bool", false) }
                .withMessage("""Value "fasle" set for config parameter "bool" is not of required type Boolean.""")
        }

        it("prints whole config-key path for NumberFormatException") {
            assertThatIllegalStateException().isThrownBy {
                config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("threshold", 6)
            }.withMessage("Value \"v5.7\" set for config parameter \"RuleSet > Rule > threshold\" is not of required type Int.")
        }

        it("prints whole config-key path for ClassCastException") {
            assertThatIllegalStateException().isThrownBy {
                @Suppress("UNUSED_VARIABLE")
                val bool: Int = config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("active", 1)
            }.withMessage("Value \"[]\" set for config parameter \"RuleSet > Rule > active\" is not of required type Int.")
        }
    }

    describe("yaml config") {

        it("loads the config from a given yaml file") {
            val path = Paths.get(resource("detekt.yml"))
            val config = YamlConfig.load(path)
            assertThat(config).isNotNull
        }

        it("loads the config from a given text file") {
            val path = Paths.get(resource("detekt.txt"))
            val config = YamlConfig.load(path)
            assertThat(config).isNotNull
        }

        it("throws an exception on an non-existing file") {
            val path = Paths.get("doesNotExist.yml")
            Assertions.assertThatIllegalArgumentException()
                .isThrownBy { YamlConfig.load(path) }
                .withMessageStartingWith("Configuration does not exist")
        }

        it("throws an exception on a directory") {
            val path = Paths.get(resource("/config_validation"))
            Assertions.assertThatIllegalArgumentException()
                .isThrownBy { YamlConfig.load(path) }
                .withMessageStartingWith("Configuration must be a file")
        }
    }
})
