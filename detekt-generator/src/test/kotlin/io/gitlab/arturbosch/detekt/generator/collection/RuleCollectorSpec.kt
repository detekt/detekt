package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidAliasesDeclaration
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidCodeExampleDocumentationException
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidIssueDeclaration
import io.gitlab.arturbosch.detekt.generator.util.run
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RuleCollectorSpec : Spek({

    val subject by memoized { RuleCollector() }

    describe("a RuleCollector") {

        it("collects no rules when no class is extended") {
            val code = "class SomeRandomClass"
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }

        it("collects no rules when no rule class is extended") {
            val code = "class SomeRandomClass : SomeOtherClass"
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }

        it("throws when a class extends Rule but has no valid documentation") {
            val rules = listOf("Rule", "FormattingRule", "ThresholdRule", "EmptyRule")
            for (rule in rules) {
                val code = "class SomeRandomClass : $rule"
                assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
            }
        }

        it("collects the rule name") {
            val name = "SomeRandomClass"
            val code = """
                /**
                 * description
                 */
                class $name : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].name).isEqualTo(name)
        }

        it("collects the rule description") {
            val description = "description"
            val code = """
                /**
                 * $description
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].description).isEqualTo(description)
        }

        it("has a multi paragraph description") {
            val description = "description"
            val code = """
                /**
                 * $description
                 *
                 * more...
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].description).startsWith(description)
            assertThat(items[0].description).contains("more...")
        }

        it("is not active by default") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].defaultActivationStatus.active).isFalse()
        }

        it("is active by default with valid version") {
            val code = """
                /**
                 * description
                 */
                @ActiveByDefault("1.12.123")
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            val defaultActivationStatus = items[0].defaultActivationStatus as Active
            assertThat(defaultActivationStatus.since).isEqualTo("1.12.123")
        }

        it("is active by default with named since") {
            val code = """
                /**
                 * description
                 */
                @ActiveByDefault(since = "1.2.3")
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            val defaultActivationStatus = items[0].defaultActivationStatus as Active
            assertThat(defaultActivationStatus.since).isEqualTo("1.2.3")
        }

        it("is active by default with invalid version") {
            val code = """
                /**
                 * description
                 */
                @ActiveByDefault("1.2.x")
                class SomeRandomClass : Rule
            """
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        it("is auto-correctable") {
            val code = """
                /**
                 * description
                 *
                 */
                @AutoCorrectable(since = "1.0.0")
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].autoCorrect).isTrue()
        }

        it("collects the issue property") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule {
                    override val defaultRuleIdAliases = setOf("RULE", "RULE2")
                    override val issue = Issue(javaClass.simpleName, Severity.Style, "", Debt.TEN_MINS)
                }
            """
            val items = subject.run(code)
            assertThat(items[0].severity).isEqualTo("Style")
            assertThat(items[0].debt).isEqualTo("10min")
            assertThat(items[0].aliases).isEqualTo("RULE, RULE2")
        }

        context("collects configuration options") {
            context("using annotation") {
                it("contains no configuration options by default") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass : Rule
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration).isEmpty()
                }

                it("contains one configuration option with correct formatting") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: String by config("[A-Z$]")
                        }                        
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration).hasSize(1)
                    val expectedConfiguration = Configuration(
                        name = "config",
                        description = "description",
                        defaultValue = "'[A-Z$]'",
                        defaultAndroidValue = null,
                        deprecated = null
                    )
                    assertThat(items[0].configuration[0]).isEqualTo(expectedConfiguration)
                }

                it("contains one configuration option of type Int") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: Int by config(99)
                        }                        
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration).hasSize(1)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("99")
                }

                it("extracts default value when it is a multi line string") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: String by config(""${"\""}abcd""${"\""})
                        }                        
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("'abcd'")
                }

                it("extracts default value when defined with named parameter") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: Int by config(defaultValue = 99)
                        }                        
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("99")
                }

                it("extracts default value for list of strings") {
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
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("['a', 'b']")
                }

                it("contains multiple configuration options") {
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
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration).hasSize(2)
                }

                it("has description that is concatenated") {
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
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].description).isEqualTo("This is a multi line description")
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("'a'")
                }

                it("extracts default value when it is an Int constant") {
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
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("99")
                }

                it("extracts default value when it is an Int constant as named parameter") {
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
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("99")
                }
                it("extracts default value when it is a String constant") {
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
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("'a'")
                }
                it("extracts default value for list of strings from constant") {
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
                    """
                    val items = subject.run(code)
                    val expected = "['a', 'b']"
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo(expected)
                    assertThat(items[0].configuration[1].defaultValue).isEqualTo(expected)
                }

                it("extracts emptyList default value") {
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
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("[]")
                    assertThat(items[0].configuration[1].defaultValue).isEqualTo("[]")
                }

                it("extracts emptyList default value of transformed list") {
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
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("[]")
                    assertThat(items[0].configuration[1].defaultValue).isEqualTo("[]")
                }

                it("is marked as deprecated as well") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Deprecated("use config1 instead")
                            @Configuration("description")
                            private val config: String by config("")
                        }                        
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration[0].deprecated).isEqualTo("use config1 instead")
                }

                it("fails if annotation and kdoc are used both to define configuration") {
                    val code = """
                        /**
                         * description
                         * @configuration config1 - description (default: `''`)
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: String by config("")
                        }                        
                    """
                    assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
                }

                it("fails if not used in combination with delegate") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: String = "foo"
                        }                        
                    """
                    assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
                }

                it("fails if not used in combination with config delegate") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: String by lazy { "foo" }
                        }                        
                    """
                    assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
                }

                it("fails if config delegate is used without annotation") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            private val config: String by config("")
                        }                        
                    """
                    assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
                }

                context("android variants") {
                    it("extracts values with android variants") {
                        val code = """
                            /**
                             * description
                             */
                            class SomeRandomClass() : Rule {
                                @Configuration("description")
                                private val maxLineLength: Int by configWithAndroidVariants(120, 100)
                            }                        
                        """
                        val items = subject.run(code)
                        assertThat(items[0].configuration[0]).isEqualTo(
                            Configuration(
                                name = "maxLineLength",
                                description = "description",
                                defaultValue = "120",
                                defaultAndroidValue = "100",
                                deprecated = null
                            )
                        )
                    }

                    it("extracts values with android variants as named arguments") {
                        val code = """
                            /**
                             * description
                             */
                            class SomeRandomClass() : Rule {
                                @Configuration("description")
                                private val maxLineLength: Int by
                                    configWithAndroidVariants(defaultValue = 120, defaultAndroidValue = 100)
                            }                        
                        """
                        val items = subject.run(code)
                        assertThat(items[0].configuration[0]).isEqualTo(
                            Configuration(
                                name = "maxLineLength",
                                description = "description",
                                defaultValue = "120",
                                defaultAndroidValue = "100",
                                deprecated = null
                            )
                        )
                    }
                }

                context("fallback property") {
                    it("extracts default value") {
                        val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val prop: Int by config(1)
                            @Configuration("description")
                            private val config1: Int by configWithFallback("prop", 99)
                            @Configuration("description")
                            private val config2: Int by configWithFallback(fallbackPropertyName = "prop", defaultValue = 99)
                            @Configuration("description")
                            private val config3: Int by configWithFallback(defaultValue = 99, fallbackPropertyName = "prop")
                        }                        
                    """
                        val items = subject.run(code)
                        val fallbackProperties = items[0].configuration.filter { it.name.startsWith("config") }
                        assertThat(fallbackProperties).hasSize(3)
                        assertThat(fallbackProperties.map { it.defaultValue }).containsOnly("99")
                    }

                    it("reports an error if the property to fallback on does not exist") {
                        val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: Int by configWithFallback("prop", 99)
                        }                        
                    """
                        assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy {
                            subject.run(
                                code
                            )
                        }
                    }

                    it("reports an error if the property to fallback on exists but is not a config property") {
                        val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            private val prop: Int = 1
                            @Configuration("description")
                            private val config: Int by configWithFallback("prop", 99)
                        }                        
                    """
                        assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy {
                            subject.run(
                                code
                            )
                        }
                    }
                }
                context("transformed property") {
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
                    """
                    it("extracts default value with transformer function") {
                        val items = subject.run(code)
                        assertThat(items[0].configuration[0].defaultValue).isEqualTo("'[a-z]+'")
                    }
                    it("extracts default value with method reference") {
                        val items = subject.run(code)
                        assertThat(items[0].configuration[1].defaultValue).isEqualTo("'false'")
                    }
                }
                context("fallback property") {
                    it("extracts default value") {
                        val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val prop: Int by config(1)
                            @Configuration("description")
                            private val config1: Int by configWithFallback("prop", 99)
                            @Configuration("description")
                            private val config2: Int by configWithFallback(fallbackPropertyName = "prop", defaultValue = 99)
                            @Configuration("description")
                            private val config3: Int by configWithFallback(defaultValue = 99, fallbackPropertyName = "prop")
                            @Configuration("description")
                            private val config4: Long by configWithFallback("prop", 99, Int::toLong)
                        }                        
                    """
                        val items = subject.run(code)
                        val fallbackProperties = items[0].configuration.filter { it.name.startsWith("config") }
                        assertThat(fallbackProperties).hasSize(4)
                        assertThat(fallbackProperties.map { it.defaultValue }).containsOnly("99")
                    }

                    it("reports an error if the property to fallback on does not exist") {
                        val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            @Configuration("description")
                            private val config: Int by configWithFallback("prop", 99)
                        }                        
                    """
                        assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy {
                            subject.run(
                                code
                            )
                        }
                    }

                    it("reports an error if the property to fallback on exists but is not a config property") {
                        val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass() : Rule {
                            private val prop: Int = 1
                            @Configuration("description")
                            private val config: Int by configWithFallback("prop", 99)
                        }                        
                    """
                        assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy {
                            subject.run(
                                code
                            )
                        }
                    }
                }
            }

            context("as part of kdoc") {
                it("contains no configuration options by default") {
                    val code = """
                        /**
                         * description
                         */
                        class SomeRandomClass : Rule
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration).isEmpty()
                }

                it("contains one configuration option with correct formatting") {
                    val code = """
                        /**
                         * description
                         * @configuration config - description (default: `'[A-Z$]'`)
                         */
                        class SomeRandomClass : Rule
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration).hasSize(1)
                    assertThat(items[0].configuration[0].name).isEqualTo("config")
                    assertThat(items[0].configuration[0].description).isEqualTo("description")
                    assertThat(items[0].configuration[0].defaultValue).isEqualTo("'[A-Z$]'")
                }

                it("contains multiple configuration options") {
                    val code = """
                        /**
                         * description
                         * @configuration config - description (default: `''`)
                         * @configuration config2 - description2 (default: `''`)
                         */
                        class SomeRandomClass: Rule
                    """
                    val items = subject.run(code)
                    assertThat(items[0].configuration).hasSize(2)
                }
                it("config option doesn't have a default value") {
                    val code = """
                        /**
                         * description
                         * @configuration config - description
                         */
                        class SomeRandomClass : Rule
                    """
                    assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
                }

                it("has a blank default value") {
                    val code = """
                        /**
                         * description
                         * @configuration config - description (default: ``)
                         */
                        class SomeRandomClass : Rule
                    """
                    assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
                }

                it("has an incorrectly delimited default value") {
                    val code = """
                        /**
                         * description
                         * @configuration config - description (default: true)
                         */
                        class SomeRandomClass : Rule
                    """
                    assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
                }

                it("contains a misconfigured configuration option") {
                    val code = """
                        /**
                         * description
                         * @configuration something: description
                         */
                        class SomeRandomClass : Rule
                    """
                    assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
                }
            }
        }

        context("collects type resolution information") {
            it("has no type resolution by default") {
                val code = """
                    /**
                     * description
                     */
                    class SomeRandomClass : Rule
                """
                val items = subject.run(code)
                assertThat(items[0].requiresTypeResolution).isFalse()
            }

            it("collects the flag that it requires type resolution") {
                val code = """
                    /**
                     * description
                     */
                    @RequiresTypeResolution
                    class SomeRandomClass : Rule
                """
                val items = subject.run(code)
                assertThat(items[0].requiresTypeResolution).isTrue()
            }

            it("collects the flag that it requires type resolution from fully qualified annotation") {
                val code = """
                    /**
                     * description
                     */
                    @io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
                    class SomeRandomClass : Rule
                """
                val items = subject.run(code)
                assertThat(items[0].requiresTypeResolution).isTrue()
            }
        }

        it("contains compliant and noncompliant code examples") {
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
            """
            val items = subject.run(code)
            assertThat(items[0].nonCompliantCodeExample).isEqualTo("val one = 2")
            assertThat(items[0].compliantCodeExample).isEqualTo("val one = 1")
        }

        it("has wrong noncompliant code example declaration") {
            val code = """
                /**
                 * description
                 *
                 * <noncompliant>
                 */
                class RandomClass : Rule
            """
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }

        it("has wrong compliant code example declaration") {
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
            """
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }

        it("has wrong compliant without noncompliant code example declaration") {
            val code = """
                /**
                 * description
                 *
                 * <compliant>
                 * val one = 1
                 * </compliant>
                 */
                class RandomClass : Rule
            """
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }

        it("has wrong issue style property") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule {

                    val style = Severity.Style
                    override val issue = Issue(javaClass.simpleName,
                            style,
                            "",
                            debt = Debt.TEN_MINS)
                }
            """
            assertThatExceptionOfType(InvalidIssueDeclaration::class.java).isThrownBy { subject.run(code) }
        }

        it("has wrong aliases property structure") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule {

                    val a = setOf("UNUSED_VARIABLE")
                    override val defaultRuleIdAliases = a
                    override val issue = Issue(javaClass.simpleName,
                            Severity.Style,
                            "",
                            debt = Debt.TEN_MINS)
                }
            """
            assertThatExceptionOfType(InvalidAliasesDeclaration::class.java).isThrownBy { subject.run(code) }
        }

        it("contains tabs in KDoc") {
            val description = "\tdescription"
            val code = """
                /**
                 * $description
                 */
                class SomeRandomClass : Rule
            """
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }
    }
})
