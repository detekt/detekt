package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.valuesWithReason
import io.gitlab.arturbosch.detekt.generator.collection.DefaultValue.Companion.of
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidCodeExampleDocumentationException
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.generator.util.run
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RuleCollectorSpec {

    private lateinit var subject: RuleCollector

    @BeforeEach
    fun createSubject() {
        subject = RuleCollector(emptyMap())
    }

    @Test
    fun `collects no rules when no class is extended`() {
        val code = "class SomeRandomClass"
        val items = subject.run(code)
        assertThat(items).isEmpty()
    }

    @Test
    fun `collects no rules when no rule class is extended`() {
        val code = "class SomeRandomClass : SomeOtherClass"
        val items = subject.run(code)
        assertThat(items).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["Rule", "FormattingRule", "EmptyRule"])
    fun `throws when a class extends Rule but has no valid documentation`(rule: String) {
        val code = "class SomeRandomClass : $rule"
        assertThatExceptionOfType(InvalidDocumentationException::class.java)
            .isThrownBy { subject.run(code) }
    }

    @Test
    fun `collects the rule name`() {
        val name = "SomeRandomClass"
        val code = """
            /**
             * description
             */
            class $name : Rule
        """.trimIndent()
        val items = subject.run(code)
        assertThat(items[0].name).isEqualTo(name)
    }

    @Test
    fun `collects the rule description`() {
        val description = "description"
        val code = """
            /**
             * $description
             */
            class SomeRandomClass : Rule
        """.trimIndent()
        val items = subject.run(code)
        assertThat(items[0].description).isEqualTo(description)
    }

    @Test
    fun `replaces text in the rule description`() {
        val code = """
            /**
             * some <foo/> description with more <foo/>. 
             */
            class SomeRandomClass : Rule
        """.trimIndent()
        val items = RuleCollector(mapOf("<foo/>" to "bar")).run(code)
        assertThat(items[0].description).isEqualTo("some bar description with more bar.")
    }

    @Test
    fun `has a multi paragraph description`() {
        val description = "description"
        val code = """
            /**
             * $description
             *
             * more...
             */
            class SomeRandomClass : Rule
        """.trimIndent()
        val items = subject.run(code)
        assertThat(items[0].description).startsWith(description)
        assertThat(items[0].description).contains("more...")
    }

    @Test
    fun `is not active by default`() {
        val code = """
            /**
             * description
             */
            class SomeRandomClass : Rule
        """.trimIndent()
        val items = subject.run(code)
        assertThat(items[0].defaultActivationStatus.active).isFalse()
    }

    @Test
    fun `is active by default with valid version`() {
        val code = """
            /**
             * description
             */
            @ActiveByDefault("1.12.123")
            class SomeRandomClass : Rule
        """.trimIndent()
        val items = subject.run(code)
        val defaultActivationStatus = items[0].defaultActivationStatus as Active
        assertThat(defaultActivationStatus.since).isEqualTo("1.12.123")
    }

    @Test
    fun `is active by default with named since`() {
        val code = """
            /**
             * description
             */
            @ActiveByDefault(since = "1.2.3")
            class SomeRandomClass : Rule
        """.trimIndent()
        val items = subject.run(code)
        val defaultActivationStatus = items[0].defaultActivationStatus as Active
        assertThat(defaultActivationStatus.since).isEqualTo("1.2.3")
    }

    @Test
    fun `is active by default with invalid version`() {
        val code = """
            /**
             * description
             */
            @ActiveByDefault("1.2.x")
            class SomeRandomClass : Rule
        """.trimIndent()
        assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
    }

    @Test
    fun `is auto-correctable`() {
        val code = """
            /**
             * description
             *
             */
            @AutoCorrectable(since = "1.0.0")
            class SomeRandomClass : Rule
        """.trimIndent()
        val items = subject.run(code)
        assertThat(items[0].autoCorrect).isTrue()
    }

    @Test
    fun `collects the issue property`() {
        val code = """
            /**
             * description
             */
            @Alias("RULE", "RULE2")
            class SomeRandomClass : Rule(Config.empty, "") {
            }
        """.trimIndent()
        val items = subject.run(code)
        assertThat(items[0].aliases).isEqualTo(listOf("RULE", "RULE2"))
    }

    @Nested
    inner class `collects configuration options using annotation` {
        @Test
        fun `contains no configuration options by default`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations).isEmpty()
        }

        @Test
        fun `contains one configuration option with correct formatting`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: String by config("[A-Z$]")
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations).hasSize(1)
            val expectedConfiguration = Configuration(
                name = "config",
                description = "description",
                defaultValue = of("[A-Z$]"),
                defaultAndroidValue = null,
                deprecated = null
            )
            assertThat(items[0].configurations).singleElement().isEqualTo(expectedConfiguration)
        }

        @Test
        fun `contains one configuration option of type Int`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: Int by config(1_999_000)
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations).hasSize(1)
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(of(1_999_000))
        }

        @Test
        fun `extracts default value when it is a multi line string`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: String by config(""${"\""}abcd""${"\""})
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(of("abcd"))
        }

        @Test
        fun `extracts default value when defined with named parameter`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: Int by config(defaultValue = 99)
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(of(99))
        }

        @Test
        fun `extracts default value for list of strings`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: List<String> by config(
                        listOf(
                            "a",
                            "b"
                        )
                    )
                }
            """.trimIndent()
            val items = subject.run(code)
            val expected = of(listOf("a", "b"))
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(expected)
        }

        @Test
        fun `contains multiple configuration options`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: String by config("")
                    
                    @Configuration("description")
                    private val config2: String by config("")
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations).hasSize(2)
        }

        @Test
        fun `has description that is concatenated`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration(
                        "This is a " +
                        "multi line " +
                        "description")
                    private val config: String by config("a")
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations[0].description).isEqualTo("This is a multi line description")
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(of("a"))
        }

        @Test
        fun `extracts default value when it is an Int constant`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: Int by config(DEFAULT_CONFIG_VALUE)
                    
                    companion object {
                        private const val DEFAULT_CONFIG_VALUE = 99
                    }
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(of(99))
        }

        @Test
        fun `extracts default value when it is an Int constant as named parameter`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: Int by config(defaultValue = DEFAULT_CONFIG_VALUE)
                    
                    companion object {
                        private const val DEFAULT_CONFIG_VALUE = 99
                    }
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(of(99))
        }

        @Test
        fun `extracts default value when it is a String constant`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: String by config(DEFAULT_CONFIG_VALUE)
                    
                    companion object {
                        private const val DEFAULT_CONFIG_VALUE = "a"
                    }
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(of("a"))
        }

        @Test
        fun `extracts default value for list of strings from constant`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config1: List<String> by config(DEFAULT_CONFIG_VALUE)
                
                    @Configuration("description")
                    private val config2: List<String> by config(listOf(DEFAULT_CONFIG_VALUE_A, "b"))
                
                    companion object {
                        private val DEFAULT_CONFIG_VALUE = listOf("a", "b")
                        private val DEFAULT_CONFIG_VALUE_A = "a"
                    }
                }
            """.trimIndent()
            val items = subject.run(code)
            val expected = of(listOf("a", "b"))
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(expected)
            assertThat(items[0].configurations[1].defaultValue).isEqualTo(expected)
        }

        @Test
        fun `extracts emptyList default value`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config1: List<String> by config(listOf())
                
                    @Configuration("description")
                    private val config2: List<String> by config(emptyList())
                }
            """.trimIndent()
            val items = subject.run(code)
            val expected = of(emptyList())
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(expected)
            assertThat(items[0].configurations[1].defaultValue).isEqualTo(expected)
        }

        @Test
        fun `extracts emptyList default value of transformed list`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config1: List<Int> by config(listOf<String>()) { it.map(String::toInt) }
                
                    @Configuration("description")
                    private val config2: List<Int> by config(DEFAULT_CONFIG_VALUE) { it.map(String::toInt) }
                
                    companion object {
                        private val DEFAULT_CONFIG_VALUE: List<String> = emptyList()
                    }
                }
            """.trimIndent()
            val items = subject.run(code)
            val expected = of(emptyList())
            assertThat(items[0].configurations[0].defaultValue).isEqualTo(expected)
            assertThat(items[0].configurations[1].defaultValue).isEqualTo(expected)
        }

        @Test
        fun `is marked as deprecated as well`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Deprecated("use config1 instead")
                    @Configuration("description")
                    private val config: String by config("")
                }
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].configurations[0].deprecated).isEqualTo("use config1 instead")
        }

        @Test
        fun `fails if kdoc is used to define configuration`() {
            val code = """
                /**
                 * description
                 * @configuration config1 - description (default: `''`)
                 */
                class SomeRandomClass() : Rule {
                }
            """.trimIndent()
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        @Test
        fun `fails if not used in combination with delegate`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: String = "foo"
                }
            """.trimIndent()
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        @Test
        fun `fails if not used in combination with config delegate`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config: String by lazy { "foo" }
                }
            """.trimIndent()
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        @Test
        fun `fails if config delegate is used without annotation`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    private val config: String by config("")
                }
            """.trimIndent()
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        @Nested
        inner class `android variants` {
            @Test
            fun `extracts values with android variants`() {
                val code = """
                    /**
                     * description
                     */
                    class SomeRandomClass() : Rule {
                        @Configuration("description")
                        private val maxLineLength: Int by configWithAndroidVariants(120, 100)
                    }
                """.trimIndent()
                val items = subject.run(code)
                assertThat(items[0].configurations).singleElement().isEqualTo(
                    Configuration(
                        name = "maxLineLength",
                        description = "description",
                        defaultValue = of(120),
                        defaultAndroidValue = of(100),
                        deprecated = null
                    )
                )
            }

            @Test
            fun `extracts values with android variants as named arguments`() {
                val code = """
                    /**
                     * description
                     */
                    class SomeRandomClass() : Rule {
                        @Configuration("description")
                        private val maxLineLength: Int by
                            configWithAndroidVariants(defaultValue = 120, defaultAndroidValue = 100)
                    }
                """.trimIndent()
                val items = subject.run(code)
                assertThat(items[0].configurations).singleElement().isEqualTo(
                    Configuration(
                        name = "maxLineLength",
                        description = "description",
                        defaultValue = of(120),
                        defaultAndroidValue = of(100),
                        deprecated = null
                    )
                )
            }
        }

        @Nested
        inner class `fallback property` {
            @Test
            fun `extracts default value`() {
                val code = """
                    /**
                     * description
                     */
                    class SomeRandomClass() : Rule {
                        @Configuration("description")
                        val prop: Int by config(1)
                        @Configuration("description")
                        private val config1: Int by configWithFallback(this::prop, 99)
                        @Configuration("description")
                        private val config2: Int by configWithFallback(fallbackProperty = ::prop, defaultValue = 99)
                        @Configuration("description")
                        private val config3: Int by configWithFallback(defaultValue = 99, fallbackProperty = ::prop)
                    }
                """.trimIndent()
                val items = subject.run(code)
                val fallbackProperties = items[0].configurations.filter { it.name.startsWith("config") }
                assertThat(fallbackProperties).hasSize(3)
                assertThat(fallbackProperties.map { it.defaultValue }).containsOnly(of(99))
            }

            @Test
            fun `reports an error if the property to fallback on exists but is not a config property`() {
                val code = """
                    /**
                     * description
                     */
                    class SomeRandomClass() : Rule {
                        val prop: Int = 1
                        @Configuration("description")
                        private val config: Int by configWithFallback(::prop, 99)
                    }
                """.trimIndent()
                assertThatThrownBy { subject.run(code) }
                    .isInstanceOf(InvalidDocumentationException::class.java)
                    .hasMessageContaining("delegate")
            }
        }

        @Nested
        inner class `transformed property` {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val config1: Regex by config("[a-z]+") { it.toRegex() }
                    @Configuration("description")
                    private val config2: String by config(false, Boolean::toString)
                }
            """.trimIndent()

            @Test
            fun `extracts default value with transformer function`() {
                val items = subject.run(code)
                assertThat(items[0].configurations[0].defaultValue).isEqualTo(of("[a-z]+"))
            }

            @Test
            fun `extracts default value with method reference`() {
                val items = subject.run(code)
                assertThat(items[0].configurations[1].defaultValue).isEqualTo(of(false))
            }
        }

        @Nested
        inner class `values with reason property` {
            private val code = """
                /**
                 * description
                 */
                class SomeRandomClass() : Rule {
                    @Configuration("description")
                    private val singleWithPositionalParam: ValuesWithReason
                        by config(valuesWithReason("value" to "reason"))
                    @Configuration("description")
                    private val singleWithNamedParam
                        by config(defaultValue = valuesWithReason("value" to "reason"))
                    @Configuration("description")
                    private val singleWithConstant by config(DEFAULT_VALUE)
                    @Configuration("description")
                    private val noValues by config(valuesWithReason())
                    @Configuration("description")
                    private val multipleValues by config(valuesWithReason("a" to "A and A", "b" to "B and B"))
                    @Configuration("description")
                    private val multipleLines
                        by config(valuesWithReason(
                            "a" to "A " +
                                "and A",
                            "b" to ""${"\""}B and B""${"\""}))
                    
                    companion object {
                        private val DEFAULT_VALUE = valuesWithReason("value" to "reason")
                    }
                }
            """.trimIndent()

            @Test
            fun `parse options of type ValuesWithReason`() {
                val rule = subject.run(code)[0]
                assertThat(rule.configurations).hasSize(6)
            }

            @Test
            fun `no values`() {
                val rule = subject.run(code)[0]
                val config = rule.configurations.first { it.name == "noValues" }
                assertThat(config.defaultValue).isEqualTo(of(valuesWithReason()))
            }

            @Test
            fun `single value`() {
                val rule = subject.run(code)[0]
                val expected = of(valuesWithReason("value" to "reason"))
                val singleValueConfigs = rule.configurations.filter { it.name.startsWith("single") }
                assertThat(singleValueConfigs)
                    .hasSize(3)
                    .extracting("defaultValue")
                    .containsOnly(expected)
            }

            @Test
            fun `multiple values`() {
                val rule = subject.run(code)[0]
                val configs = rule.configurations.filter { it.name.startsWith("multiple") }
                val expected = of(
                    valuesWithReason(
                        "a" to "A and A",
                        "b" to "B and B"
                    )
                )
                assertThat(configs)
                    .hasSize(2)
                    .extracting("defaultValue")
                    .containsOnly(expected)
            }
        }
    }

    @Nested
    inner class `collects type resolution information` {
        @Test
        fun `has no type resolution by default`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].requiresTypeResolution).isFalse()
        }

        @Test
        fun `collects the flag that it requires type resolution`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule, RequiresTypeResolution
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].requiresTypeResolution).isTrue()
        }

        @Test
        fun `collects the flag that it requires type resolution from fully qualified annotation`() {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule(), io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
            """.trimIndent()
            val items = subject.run(code)
            assertThat(items[0].requiresTypeResolution).isTrue()
        }
    }

    @Test
    fun `contains compliant and noncompliant code examples`() {
        val code = """
            /**
             * description
             *
             * <noncompliant>
             * val one = 2
             * </noncompliant>
             *
             * <compliant>
             * val one = 1
             * </compliant>
             */
            class RandomClass : Rule
        """.trimIndent()
        val items = subject.run(code)
        assertThat(items[0].nonCompliantCodeExample).isEqualTo("val one = 2")
        assertThat(items[0].compliantCodeExample).isEqualTo("val one = 1")
    }

    @Test
    fun `has wrong noncompliant code example declaration`() {
        val code = """
            /**
             * description
             *
             * <noncompliant>
             */
            class RandomClass : Rule
        """.trimIndent()
        assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
            .isThrownBy { subject.run(code) }
    }

    @Test
    fun `has wrong compliant code example declaration`() {
        val code = """
            /**
             * description
             *
             * <noncompliant>
             * val one = 2
             * </noncompliant>
             * <compliant>
             */
            class RandomClass : Rule
        """.trimIndent()
        assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
            .isThrownBy { subject.run(code) }
    }

    @Test
    fun `has wrong compliant without noncompliant code example declaration`() {
        val code = """
            /**
             * description
             *
             * <compliant>
             * val one = 1
             * </compliant>
             */
            class RandomClass : Rule
        """.trimIndent()
        assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
            .isThrownBy { subject.run(code) }
    }

    @Test
    fun `contains tabs in KDoc`() {
        val description = "\tdescription"
        val code = """
            /**
             * $description
             */
            class SomeRandomClass : Rule
        """.trimIndent()
        assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
    }
}
