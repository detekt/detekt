package dev.detekt.generator.collection

import dev.detekt.generator.collection.DefaultValue.Companion.of
import dev.detekt.generator.collection.exception.InvalidDocumentationException
import dev.detekt.generator.util.run
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RuleSetProviderCollectorSpec {

    private lateinit var subject: RuleSetProviderCollector

    @BeforeEach
    fun createSubject() {
        subject = RuleSetProviderCollector()
    }

    @Nested
    inner class `a non-RuleSetProvider class extending nothing` {
        private val code = """
            package foo
            
            class SomeRandomClass {
                fun logSomething(message: String) {
                    println(message)
                }
            }
        """.trimIndent()

        @Test
        fun `collects no rulesets`() {
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }
    }

    @Nested
    inner class `a non-RuleSetProvider class extending a class that is not related to rules` {
        private val code = """
            package foo
            
            class SomeRandomClass: SomeOtherClass {
                fun logSomething(message: String) {
                    println(message)
                }
            }
        """.trimIndent()

        @Test
        fun `collects no rulesets`() {
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }
    }

    @Nested
    inner class `a RuleSetProvider without documentation` {
        private val code = """
            package foo
            
            class TestProvider: RuleSetProvider {
                fun logSomething(message: String) {
                    println(message)
                }
            }
        """.trimIndent()

        @Test
        fun `throws an exception`() {
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }
    }

    @Nested
    inner class `a correct RuleSetProvider class extending RuleSetProvider but missing parameters` {
        private val code = """
            package foo
            
            class TestProvider: RuleSetProvider {
                fun logSomething(message: String) {
                    println(message)
                }
            }
        """.trimIndent()

        @Test
        fun `throws an exception`() {
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }
    }

    @Nested
    inner class `a correct RuleSetProvider class with full parameters` {
        private val description = "This is a description"
        private val ruleSetId = "test"
        private val ruleName = "TestRule"
        private val code = """
            package foo
            
            /**
             * $description
             *
             *
             */
            @ActiveByDefault("1.0.0")
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("$ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            ::$ruleName
                    ))
                }
            }
        """.trimIndent()

        @Test
        fun `collects a RuleSetProvider`() {
            val items = subject.run(code)
            assertThat(items).hasSize(1)
        }

        @Test
        fun `has one rule`() {
            val items = subject.run(code)
            val provider = items[0]
            assertThat(provider.rules).singleElement().isEqualTo(ruleName)
        }

        @Test
        fun `has correct name`() {
            val items = subject.run(code)
            val provider = items[0]
            assertThat(provider.name).isEqualTo(ruleSetId)
        }

        @Test
        fun `has correct description`() {
            val items = subject.run(code)
            val provider = items[0]
            assertThat(provider.description).isEqualTo(description)
        }

        @Test
        fun `is active`() {
            val items = subject.run(code)
            val provider = items[0]
            assertThat(provider.defaultActivationStatus.active).isTrue()
        }
    }

    @Nested
    inner class `an inactive RuleSetProvider` {
        private val description = "This is a description"
        private val ruleSetId = "test"
        private val ruleName = "TestRule"
        private val code = """
            package foo
            
            /**
             * $description
             */
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("$ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            ::$ruleName
                    ))
                }
            }
        """.trimIndent()

        @Test
        fun `is not active`() {
            val items = subject.run(code)
            val provider = items[0]
            assertThat(provider.defaultActivationStatus.active).isFalse()
        }
    }

    @Nested
    inner class `a RuleSetProvider with missing name` {
        private val description = "This is a description"
        private val ruleName = "TestRule"
        private val code = """
            package foo
            
            /**
             * $description
             */
            class TestProvider: RuleSetProvider {
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            ::$ruleName
                    ))
                }
            }
        """.trimIndent()

        @Test
        fun `throws an exception`() {
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }
    }

    @Nested
    inner class `a RuleSetProvider with missing description` {
        private val ruleSetId = "test"
        private val ruleName = "TestRule"
        private val code = """
            package foo
            
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("$ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            ::$ruleName
                    ))
                }
            }
        """.trimIndent()

        @Test
        fun `throws an exception`() {
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }
    }

    @Nested
    inner class `a RuleSetProvider with invalid activation version` {
        private val code = """
            package foo
            
            /**
             * description
             */
            @ActiveByDefault(since = "1.2.xyz")
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            ::TestRule
                    ))
                }
            }
        """.trimIndent()

        @Test
        fun `throws an exception`() {
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }
    }

    @Nested
    inner class `a RuleSetProvider with no rules` {
        private val ruleSetId = "test"
        private val code = """
            package foo
            
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("$ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, emptyListOf())
                }
            }
        """.trimIndent()

        @Test
        fun `throws an exception`() {
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }
    }

    @Nested
    inner class `a correct RuleSetProvider class with full parameters and multiple rules` {
        private val description = "This is a description"
        private val ruleSetId = "test"
        private val ruleName = "TestRule"
        private val secondRuleName = "SecondRule"
        private val code = """
            package foo
            
            /**
             * $description
             */
            @ActiveByDefault("1.0.0")
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("$ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            ::$ruleName,
                            ::$secondRuleName
                    ))
                }
            }
        """.trimIndent()

        @Test
        fun `collects multiple rules`() {
            val items = subject.run(code)
            assertThat(items[0].rules).containsExactly(ruleName, secondRuleName)
        }
    }

    @Nested
    inner class `a correct RuleSetProvider class with sorted rules` {
        private val description = "This is a description"
        private val ruleSetId = "test"
        private val ruleName = "TestRule"
        private val secondRuleName = "SecondRule"
        private val code = """
            package foo
            
            /**
             * $description
             */
            @ActiveByDefault("1.0.0")
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("$ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            ::$ruleName,
                            ::$secondRuleName
                    ).sortedBy(SomeComparator))
                }
            }
        """.trimIndent()

        @Test
        fun `collects multiple rules`() {
            val items = subject.run(code)
            assertThat(items[0].rules).containsExactly(ruleName, secondRuleName)
        }
    }

    @Nested
    inner class `a RuleSetProvider with configurations in kdoc` {
        private val code = """
            package foo
            
            /**
             * description
             * @configuration android - if android style guides should be preferred (default: `false`)
             */
            class TestProvider: RuleSetProvider {
        """.trimIndent()

        @Test
        fun `throws exception for configuration in kdoc`() {
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }
    }

    @Nested
    inner class `a RuleSetProvider with configurations` {
        private val code = """
            package foo
            
            /**
             * description
             */
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(::RruleName))
                }
            
                companion object {
                    @Configuration("bool description")
                    val aBool by ruleSetConfig(true)
            
                    @Configuration("int description")
                    val anInt by ruleSetConfig(99)
            
                    @Deprecated("use something else")
                    @Configuration("string description")
                    val aString by ruleSetConfig("a")
                }
            }
        """.trimIndent()
        private val items by lazy { subject.run(code) }

        @Test
        fun `extracts boolean configuration option`() {
            val conf = items[0].configuration[0]
            assertThat(conf.name).isEqualTo("aBool")
            assertThat(conf.description).isEqualTo("bool description")
            assertThat(conf.defaultValue).isEqualTo(of(true))
            assertThat(conf.deprecated).isNull()
        }

        @Test
        fun `extracts int configuration option`() {
            val conf = items[0].configuration[1]
            assertThat(conf.name).isEqualTo("anInt")
            assertThat(conf.description).isEqualTo("int description")
            assertThat(conf.defaultValue).isEqualTo(of(99))
        }

        @Test
        fun `extracts string configuration option`() {
            val conf = items[0].configuration[2]
            assertThat(conf.name).isEqualTo("aString")
            assertThat(conf.description).isEqualTo("string description")
            assertThat(conf.defaultValue).isEqualTo(of("a"))
            assertThat(conf.deprecated).isEqualTo("use something else")
        }
    }

    @Nested
    inner class `a RuleSetProvider with unsupported configuration format` {
        private val code = """
            package foo
            
            /**
             * description
             */
            class TestProvider: RuleSetProvider {
                override val ruleSetId = RuleSetId("ruleSetId")
            
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(::RruleName))
                }
            
                companion object {
                    @Configuration("a description")
                    val aConfig by ruleSetConfig(listOf("a"))
                }
            }
        """.trimIndent()

        @Test
        fun fails() {
            assertThatThrownBy { subject.run(code) }
                .isInstanceOf(InvalidDocumentationException::class.java)
                .hasMessageContaining("""Unsupported default value format 'listOf("a")'""")
        }
    }
}
