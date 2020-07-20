package io.gitlab.arturbosch.detekt.api.internal

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.yamlConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.yaml.snakeyaml.parser.ParserException
import java.nio.file.Paths

@Suppress("DEPRECATION")
class YamlConfigSpec : Spek({

    describe("load yaml config") {

        val config by memoized { yamlConfig("detekt.yml") }

        it("should create a sub config") {
            val subConfig = config.subConfig("style")
            assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())).isNotEmpty
            assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())["active"].toString()).isEqualTo("true")
            assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())["active"] as Boolean).isTrue()
            assertThat(subConfig.valueOrDefault("NotFound", mapOf<String, Any>())).isEmpty()
            assertThat(subConfig.valueOrDefault("NotFound", "")).isEmpty()
        }

        it("should create a sub sub config") {
            val subConfig = config.subConfig("style")
            val subSubConfig = subConfig.subConfig("WildcardImport")
            assertThat(subSubConfig.valueOrDefault("active", false)).isTrue()
            assertThat(subSubConfig.valueOrDefault("NotFound", true)).isTrue()
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
            val path = resourceAsPath("detekt.yml")
            val config = YamlConfig.load(path)
            assertThat(config).isNotNull
        }

        it("loads the config from a given text file") {
            val path = resourceAsPath("detekt.txt")
            val config = YamlConfig.load(path)
            assertThat(config).isNotNull
        }

        it("throws an exception on an non-existing file") {
            val path = Paths.get("doesNotExist.yml")
            assertThatIllegalArgumentException()
                .isThrownBy { YamlConfig.load(path) }
                .withMessageStartingWith("Configuration does not exist")
        }

        it("throws an exception on a directory") {
            val path = resourceAsPath("/config_validation")
            assertThatIllegalArgumentException()
                .isThrownBy { YamlConfig.load(path) }
                .withMessageStartingWith("Configuration must be a file")
        }

        it("throws InvalidConfigurationError on invalid structured yaml files") {
            assertThatCode {
                yamlConfigFromContent("""
                    map:
                          {}map
                """.trimIndent())
            }.isInstanceOf(Config.InvalidConfigurationError::class.java)
                .hasMessageContaining("Provided configuration file is invalid")
                .hasCauseInstanceOf(ParserException::class.java)
        }
    }
})
