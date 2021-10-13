package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.generator.util.run
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RuleSetProviderCollectorSpec : Spek({

    val subject by memoized { RuleSetProviderCollector() }

    describe("RuleSetProviderCollector rule") {
        context("a non-RuleSetProvider class extending nothing") {
            val code = """
            package foo

            class SomeRandomClass {
                fun logSomething(message: String) {
                    println(message)
                }
            }
        """
            it("collects no rulesets") {
                val items = subject.run(code)
                assertThat(items).isEmpty()
            }
        }

        context("a non-RuleSetProvider class extending a class that is not related to rules") {
            val code = """
            package foo

            class SomeRandomClass: SomeOtherClass {
                fun logSomething(message: String) {
                    println(message)
                }
            }
        """
            it("collects no rulesets") {
                val items = subject.run(code)
                assertThat(items).isEmpty()
            }
        }

        context("a RuleSetProvider without documentation") {
            val code = """
            package foo

            class TestProvider: RuleSetProvider {
                fun logSomething(message: String) {
                    println(message)
                }
            }
        """
            it("throws an exception") {
                assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
            }
        }

        context("a correct RuleSetProvider class extending RuleSetProvider but missing parameters") {
            val code = """
            package foo

            class TestProvider: RuleSetProvider {
                fun logSomething(message: String) {
                    println(message)
                }
            }
        """

            it("throws an exception") {
                assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
            }
        }

        context("a correct RuleSetProvider class with full parameters") {
            val description = "This is a description"
            val ruleSetId = "test"
            val ruleName = "TestRule"
            val code = """
            package foo

            /**
             * $description
             *
             * 
             */
            @ActiveByDefault("1.0.0")
            class TestProvider: RuleSetProvider {
                override val ruleSetId: String = "$ruleSetId"

                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            $ruleName(config)
                    ))
                }
            }
        """

            it("collects a RuleSetProvider") {
                val items = subject.run(code)
                assertThat(items).hasSize(1)
            }

            it("has one rule") {
                val items = subject.run(code)
                val provider = items[0]
                assertThat(provider.rules).hasSize(1)
                assertThat(provider.rules[0]).isEqualTo(ruleName)
            }

            it("has correct name") {
                val items = subject.run(code)
                val provider = items[0]
                assertThat(provider.name).isEqualTo(ruleSetId)
            }

            it("has correct description") {
                val items = subject.run(code)
                val provider = items[0]
                assertThat(provider.description).isEqualTo(description)
            }

            it("is active") {
                val items = subject.run(code)
                val provider = items[0]
                assertThat(provider.defaultActivationStatus.active).isTrue()
            }
        }

        context("an inactive RuleSetProvider") {
            val description = "This is a description"
            val ruleSetId = "test"
            val ruleName = "TestRule"
            val code = """
            package foo

            /**
             * $description
             */
            class TestProvider: RuleSetProvider {
                override val ruleSetId: String = "$ruleSetId"

                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            $ruleName(config)
                    ))
                }
            }
        """

            it("is not active") {
                val items = subject.run(code)
                val provider = items[0]
                assertThat(provider.defaultActivationStatus.active).isFalse()
            }
        }

        context("a RuleSetProvider with missing name") {
            val description = "This is a description"
            val ruleName = "TestRule"
            val code = """
            package foo

            /**
             * $description
             */
            class TestProvider: RuleSetProvider {
                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            $ruleName(config)
                    ))
                }
            }
        """

            it("throws an exception") {
                assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
            }
        }

        context("a RuleSetProvider with missing description") {
            val ruleSetId = "test"
            val ruleName = "TestRule"
            val code = """
            package foo

            class TestProvider: RuleSetProvider {
                override val ruleSetId: String = "$ruleSetId"

                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            $ruleName(config)
                    ))
                }
            }
        """

            it("throws an exception") {
                assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
            }
        }

        context("a RuleSetProvider with invalid activation version") {
            val code = """
            package foo

            /**
             * description
             */
            @ActiveByDefault(since = "1.2.xyz")
            class TestProvider: RuleSetProvider {
                override val ruleSetId: String = "ruleSetId"

                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            TestRule(config)
                    ))
                }
            }
        """

            it("throws an exception") {
                assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
            }
        }

        context("a RuleSetProvider with no rules") {
            val ruleSetId = "test"
            val code = """
            package foo

            class TestProvider: RuleSetProvider {
                override val ruleSetId: String = "$ruleSetId"

                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, emptyListOf())
                }
            }
        """

            it("throws an exception") {
                assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
            }
        }

        context("a correct RuleSetProvider class with full parameters") {
            val description = "This is a description"
            val ruleSetId = "test"
            val ruleName = "TestRule"
            val secondRuleName = "SecondRule"
            val code = """
            package foo

            /**
             * $description
             */
            @ActiveByDefault("1.0.0")
            class TestProvider: RuleSetProvider {
                override val ruleSetId: String = "$ruleSetId"

                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(
                            $ruleName(config),
                            $secondRuleName(config)
                    ))
                }
            }
        """

            it("collects multiple rules") {
                val items = subject.run(code)
                assertThat(items[0].rules).containsExactly(ruleName, secondRuleName)
            }
        }

        context("a RuleSetProvider with configurations in kdoc") {
            val code = """
            package foo

            /**
             * description
             * @configuration android - if android style guides should be preferred (default: `false`)
             */
            class TestProvider: RuleSetProvider {
            """.trimIndent()

            it("throws exception for configuration in kdoc") {
                assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
            }
        }

        context("a RuleSetProvider with configurations") {
            val code = """
            package foo

            /**
             * description
             */
            class TestProvider: RuleSetProvider {
                override val ruleSetId: String = "ruleSetId"

                override fun instance(config: Config): RuleSet {
                    return RuleSet(ruleSetId, listOf(RruleName(config)))
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
        """
            val items by memoized { subject.run(code) }

            it("extracts boolean configuration option") {
                val conf = items[0].configuration[0]
                assertThat(conf.name).isEqualTo("aBool")
                assertThat(conf.description).isEqualTo("bool description")
                assertThat(conf.defaultValue).isEqualTo("true")
                assertThat(conf.deprecated).isNull()
            }

            it("extracts int configuration option") {
                val conf = items[0].configuration[1]
                assertThat(conf.name).isEqualTo("anInt")
                assertThat(conf.description).isEqualTo("int description")
                assertThat(conf.defaultValue).isEqualTo("99")
            }

            it("extracts string configuration option") {
                val conf = items[0].configuration[2]
                assertThat(conf.name).isEqualTo("aString")
                assertThat(conf.description).isEqualTo("string description")
                assertThat(conf.defaultValue).isEqualTo("'a'")
                assertThat(conf.deprecated).isEqualTo("use something else")
            }
        }
    }
})
