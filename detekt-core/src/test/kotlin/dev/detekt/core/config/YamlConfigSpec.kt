package dev.detekt.core.config

import dev.detekt.api.valueOrDefault
import dev.detekt.api.valueOrNull
import dev.detekt.core.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.snakeyaml.engine.v2.exceptions.ParserException
import kotlin.reflect.KClass

class YamlConfigSpec {

    @Nested
    inner class `load yaml config` {

        private val config = yamlConfigFromContent(
            """
                code-smell:
                  LongMethod:
                    active: true
                    allowedLines: 20
                  LongParameterList:
                    active: false
                    threshold: 5
                  LargeClass:
                    active: false
                    threshold: 70
                  InnerMap:
                    Inner1:
                      active: true
                    Inner2:
                      active: true

                style:
                  WildcardImport:
                    active: true
                  NoElseInWhenExpression:
                    active: true
                  MagicNumber:
                    active: true
                    ignoreNumbers: ['-1', '0', '1', '2']
            """.trimIndent()
        )

        @Test
        fun `should create a sub config`() {
            val subConfig = config.subConfig("style")
            assertThat(subConfig.valueOrDefault("WildcardImport", emptyMap<String, Any>()))
                .isNotEmpty
            assertThat(subConfig.valueOrDefault("WildcardImport", emptyMap<String, Any>())["active"].toString())
                .isEqualTo("true")
            assertThat(subConfig.valueOrDefault("WildcardImport", emptyMap<String, Any>())["active"] as Boolean)
                .isTrue()
            assertThat(subConfig.valueOrDefault("NotFound", emptyMap<String, Any>()))
                .isEmpty()
            assertThat(subConfig.valueOrDefault("NotFound", ""))
                .isEmpty()
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
            assertThatIllegalArgumentException().isThrownBy { config.valueOrDefault("style", "") }
                .withMessage(
                    "Value '{WildcardImport={active=true}, NoElseInWhenExpression={active=true}, MagicNumber={active=true, ignoreNumbers=[-1, 0, 1, 2]}}' set for config parameter 'style' is not of required type `kotlin.String`"
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
                .withMessage("Value 'fasle' set for config parameter 'bool' is not of required type `kotlin.Boolean`")
        }

        @Test
        fun `prints whole config-key path for NumberFormatException`() {
            assertThatIllegalArgumentException().isThrownBy {
                config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("threshold", 6)
            }
                .withMessage(
                    "Value 'v5.7' set for config parameter 'RuleSet > Rule > threshold' is not of required type `kotlin.Int`"
                )
        }

        @Test
        fun `prints whole config-key path for ClassCastException`() {
            assertThatIllegalArgumentException().isThrownBy {
                config.subConfig("RuleSet")
                    .subConfig("Rule")
                    .valueOrDefault("active", 1)
            }
                .withMessage(
                    "Value '[]' set for config parameter 'RuleSet > Rule > active' is not of required type `kotlin.Int`"
                )
        }
    }

    @Nested
    inner class `yaml config` {

        @Test
        fun `empty yaml file is equivalent to empty config`() {
            yamlConfigFromContent("")
        }

        @Test
        fun `single item in yaml file is valid`() {
            yamlConfigFromContent("style:")
        }

        @Test
        fun `loads the config from a given yaml file`() {
            yamlConfigFromContent(
                """
                    code-smell:
                      LongMethod:
                """.trimIndent()
            )
        }

        @Test
        fun `throws when yaml can't be parsed`() {
            assertThatCode {
                yamlConfigFromContent(
                    """
                        map:
                          {}map
                    """.trimIndent()
                )
            }.isInstanceOf(ParserException::class.java)
        }

        @Test
        fun `throws InvalidConfigurationError when content is not a map`() {
            assertThatCode {
                yamlConfigFromContent(
                    """
                      - item
                    """.trimIndent()
                )
            }.isInstanceOf(InvalidConfigurationError::class.java)
                .hasMessage("Provided configuration file is invalid: Structure must be from type Map<String, Any>!")
                .hasCauseInstanceOf(ClassCastException::class.java)
        }
    }

    @Nested
    inner class `Values with reason` {
        private val config = yamlConfigFromContent(
            """
                style:
                  AsList:
                    values:
                      - a
                      - b
                      - c
                  AsListOfMaps:
                    values:
                      - value: a
                        reason: reason A
                      - value: b
                      - value: c
                        reason: reason C
                  EmptyListOfMaps:
                    values: []
                  MixedWithStringAndMaps:
                    values:
                      - a
                      - b
                      - value: c
                      - value: d
                      - value: e
                        reason: reason E
                      - value: f
                        reason: reason F
            """.trimIndent()
        )

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

    @Nested
    inner class TestValueOrNull {
        val firstLong = Int.MAX_VALUE + 1L
        private val config = yamlConfigFromContent(
            """
                int: 1
                long: $firstLong
                double: 2.72
                string: "Hi"
                boolean: true
                listString: ["Hello", "there"]
                listInt: [1, -1]
                listWithReason:
                  - "string"
                  - value: "What"
                    reason: "Que"
                map:
                  hello: "hola"
                  bye: "adios"
                  threshold: 1
            """.trimIndent()
        )

        @TestFactory
        fun valueOrNull(): List<DynamicTest> {
            val parsedValues = mapOf(
                "int" to 1,
                "long" to firstLong,
                "double" to 2.72,
                "string" to "Hi",
                "boolean" to true,
                "listString" to listOf("Hello", "there"),
                "listInt" to listOf(1, -1),
                "listWithReason" to listOf("string", mapOf("value" to "What", "reason" to "Que")),
                "map" to mapOf("hello" to "hola", "bye" to "adios", "threshold" to 1),
                "unknown" to "null",
            )

            return listOf(
                TestCase(Int::class, "int" to 1),
                TestCase(Long::class, "int" to 1L, "long" to firstLong),
                TestCase(Float::class, "int" to 1f, "long" to firstLong.toFloat(), "double" to 2.72f),
                TestCase(Double::class, "int" to 1.0, "long" to firstLong.toDouble(), "double" to 2.72),
                TestCase(String::class, "string" to "Hi"),
                TestCase(Boolean::class, "boolean" to true),
                TestCase(
                    List::class,
                    "listString" to listOf("Hello", "there"),
                    "listInt" to listOf(1, -1),
                    "listWithReason" to listOf("string", mapOf("value" to "What", "reason" to "Que")),
                ),
                TestCase(Map::class, "map" to mapOf("hello" to "hola", "bye" to "adios", "threshold" to 1)),
            )
                .flatMap { testCase -> parsedValues.keys.map { testCase to it } }
                .map { (testCase, key) ->
                    if (testCase.keyValue.containsKey(key)) {
                        val value = testCase.keyValue[key]
                        dynamicTest("correct value for key $key with type ${testCase.name}") {
                            assertThat(testCase.read(config, key)).isEqualTo(value)
                        }
                    } else {
                        val value = parsedValues[key]
                        dynamicTest(
                            "throws IllegalArgumentException because class ${testCase.name} cannot be cast to $key"
                        ) {
                            assertThatIllegalArgumentException()
                                .isThrownBy { testCase.read(config, key) }
                                .withMessage(
                                    "Value '$value' set for config parameter '$key' is not of required type " +
                                        "`${testCase.name}`"
                                )
                        }
                    }
                }
        }
    }

    private data class TestCase<T : Any>(
        val name: String,
        val keyValue: Map<String, T?>,
        val read: (YamlConfig, String) -> T?,
    ) {
        constructor(klass: KClass<T>, vararg keys: Pair<String, T>) : this(
            klass.qualifiedName!!,
            keys.asList().plus("unknown" to null).toMap(),
            { config: YamlConfig, key: String -> config.valueOrNull(key, klass) },
        )
    }
}
