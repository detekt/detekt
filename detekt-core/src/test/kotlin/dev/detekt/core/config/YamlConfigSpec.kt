package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.test.yamlConfig
import dev.detekt.test.yamlConfigFromContent
import dev.detekt.utils.getSafeResourceAsStream
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.snakeyaml.engine.v2.exceptions.ParserException
import kotlin.io.path.Path

class YamlConfigSpec {

    @Nested
    inner class `load yaml config` {

        private val config = yamlConfig("detekt.yml")

        @Test
        fun `should create a sub config`() {
            val subConfig = config.subConfig("style")
            assertThat(subConfig.valueOrDefault("WildcardImport", emptyMap<String, Any>())).isNotEmpty
            assertThat(
                subConfig.valueOrDefault(
                    "WildcardImport",
                    emptyMap<String, Any>()
                )["active"].toString()
            ).isEqualTo("true")
            assertThat(
                subConfig.valueOrDefault(
                    "WildcardImport",
                    emptyMap<String, Any>()
                )["active"] as Boolean
            ).isTrue()
            assertThat(subConfig.valueOrDefault("NotFound", emptyMap<String, Any>())).isEmpty()
            assertThat(subConfig.valueOrDefault("NotFound", "")).isEmpty()
        }

        @Test
        fun `should create a sub sub config`() {
            val subConfig = config.subConfig("style")
            val subSubConfig = subConfig.subConfig("WildcardImport")
            assertThat(subSubConfig.valueOrDefault("active", false)).isTrue()
            assertThat(subSubConfig.valueOrDefault("NotFound", true)).isTrue()
        }

        @Test
        fun `tests wrong sub config conversion`() {
            assertThatIllegalStateException().isThrownBy {
                @Suppress("UNUSED_VARIABLE")
                val ignored = config.valueOrDefault("style", "")
            }
                .withMessage(
                    "Value \"{WildcardImport={active=true}, NoElseInWhenExpression={active=true}, MagicNumber={active=true, ignoreNumbers=[-1, 0, 1, 2]}}\" set for config parameter \"style\" is not of required type String."
                )
        }

        @Test
        fun `parent path of ruleset config is ruleset id`() {
            val rulesetId = "style"
            val subject = config.subConfig(rulesetId)
            val actual = subject.parentPath
            assertThat(actual).isEqualTo(rulesetId)
        }

        @Test
        fun `parent returns the original config`() {
            val rulesetId = "style"
            val subject = config.subConfig(rulesetId)
            val actual = subject.parent
            assertThat(actual).isEqualTo(config)
        }

        @Test
        fun `subConfigs returns all sub configs`() {
            val subject = config.subConfig("style")
            val actual = subject.subConfigKeys()
            assertThat(actual).containsExactly("WildcardImport", "NoElseInWhenExpression", "MagicNumber")
        }
    }

    @Nested
    inner class `loading empty configurations` {

        @Test
        fun `empty yaml file is equivalent to empty config`() {
            javaClass.getSafeResourceAsStream("/empty.yml")!!.reader().use(YamlConfig::load)
        }

        @Test
        fun `single item in yaml file is valid`() {
            javaClass.getSafeResourceAsStream("/oneitem.yml")!!.reader().use(YamlConfig::load)
        }
    }

    @Nested
    inner class `meaningful error messages` {

        private val config = yamlConfigFromContent(
            """
                RuleSet:
                  Rule:
                    active: []
                    threshold: v5.7
                
                bool: fasle
            """.trimIndent()
        )

        @Test
        fun `only accepts true and false boolean values`() {
            assertThatIllegalArgumentException()
                .isThrownBy { config.valueOrDefault("bool", false) }
                .withMessage("""The string doesn't represent a boolean value: fasle""")
        }

        @Test
        fun `prints whole config-key path for NumberFormatException`() {
            assertThatIllegalStateException().isThrownBy {
                config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("threshold", 6)
            }
                .withMessage(
                    "Value \"v5.7\" set for config parameter \"RuleSet > Rule > threshold\" is not of required type Int."
                )
        }

        @Test
        fun `prints whole config-key path for ClassCastException`() {
            assertThatIllegalStateException().isThrownBy {
                @Suppress("UNUSED_VARIABLE")
                val bool: Int = config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("active", 1)
            }
                .withMessage(
                    "Value \"[]\" set for config parameter \"RuleSet > Rule > active\" is not of required type Int."
                )
        }

        @Test
        fun `prints meaningful message when list of ints is used instead of list of strings`() {
            assertThatIllegalStateException().isThrownBy {
                config.valueOrDefaultInternal(key = "key", result = listOf(1, 2), default = listOf("1", "2"))
            }.withMessage(
                "Only lists of strings are supported. " +
                    "Value \"[1, 2]\" set for config parameter \"key\" contains non-string values."
            )
        }

        @Test
        fun `prints meaningful message when string is used instead of list of strings`() {
            assertThatIllegalStateException().isThrownBy {
                config.valueOrDefaultInternal(key = "key", result = "", default = emptyList<String>())
            }.withMessage(
                """
                    Value "" set for config parameter "key" is not of required type List.
                """.trimIndent()
            )
        }
    }

    @Nested
    inner class `yaml config` {

        @Test
        fun `loads the config from a given yaml file`() {
            val path = resourceAsPath("detekt.yml")
            val config = YamlConfig.load(path)
            assertThat(config).isNotNull
        }

        @Test
        fun `loads the config from a given text file`() {
            val path = resourceAsPath("detekt.txt")
            val config = YamlConfig.load(path)
            assertThat(config).isNotNull
        }

        @Nested
        inner class `Values with reason` {
            private val config = YamlConfig.load(resourceAsPath("values-with-reason.yml"))

            @Test
            fun `can be parsed`() {
                assertThat(config).isNotNull
            }

            @Test
            fun `supports lists`() {
                val actualAsList: List<*>? = config
                    .subConfig("style")
                    .subConfig("AsList")
                    .valueOrNull("values")
                assertThat(actualAsList).hasSize(3)
            }

            @Test
            fun `supports dictionaries`() {
                val actualAsMap: List<Map<*, *>>? = config
                    .subConfig("style")
                    .subConfig("AsListOfMaps")
                    .valueOrNull("values")
                assertThat(actualAsMap)
                    .hasSize(3)
            }

            @Test
            fun `supports empty dictionaries`() {
                val actualAsMap: List<Map<*, *>>? = config
                    .subConfig("style")
                    .subConfig("EmptyListOfMaps")
                    .valueOrNull("values")
                assertThat(actualAsMap)
                    .isNotNull
                    .isEmpty()
            }

            @Test
            fun `supports mixed string and dictionary`() {
                val actualAsMap: List<Map<*, *>>? = config
                    .subConfig("style")
                    .subConfig("MixedWithStringAndMaps")
                    .valueOrNull("values")
                assertThat(actualAsMap)
                    .isNotNull
                    .isNotEmpty
                    .hasSize(6)
                    .elements(0, 1)
                    .hasOnlyElementsOfType(String::class.java)

                assertThat(actualAsMap)
                    .elements(2, 3, 4, 5)
                    .hasOnlyElementsOfType(Map::class.java)
            }
        }

        @Test
        fun `throws an exception on an non-existing file`() {
            val path = Path("doesNotExist.yml")
            assertThatIllegalArgumentException()
                .isThrownBy { YamlConfig.load(path) }
                .withMessageStartingWith("Configuration does not exist")
        }

        @Test
        fun `throws an exception on a directory`() {
            val path = resourceAsPath("/config_validation")
            assertThatIllegalArgumentException()
                .isThrownBy { YamlConfig.load(path) }
                .withMessageStartingWith("Configuration must be a file")
        }

        @Test
        fun `throws InvalidConfigurationError on invalid structured yaml files`() {
            assertThatCode {
                yamlConfigFromContent(
                    """
                        map:
                              {}map
                    """.trimIndent()
                )
            }.isInstanceOf(Config.InvalidConfigurationError::class.java)
                .hasMessageContaining("Provided configuration file is invalid")
                .hasCauseInstanceOf(ParserException::class.java)
        }
    }
}
