package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LongParameterListSpec : Spek({

    val defaultThreshold = 2
    val defaultConfig by memoized {
        TestConfig(mapOf(
            LongParameterList.FUNCTION_THRESHOLD to defaultThreshold,
            LongParameterList.CONSTRUCTOR_THRESHOLD to defaultThreshold
        ))
    }

    val subject by memoized { LongParameterList(defaultConfig) }

    describe("LongParameterList rule") {

        val reportMessageForFunction = "The function long(a: Int, b: Int) has too many parameters. " +
            "The current threshold is set to $defaultThreshold."
        val reportMessageForConstructor = "The constructor(a: Int, b: Int) has too many parameters. " +
            "The current threshold is set to $defaultThreshold."

        it("reports too long parameter list") {
            val code = "fun long(a: Int, b: Int) {}"
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(reportMessageForFunction)
        }

        it("does not report short parameter list") {
            val code = "fun long(a: Int) {}"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports too long parameter list event for parameters with defaults") {
            val code = "fun long(a: Int, b: Int = 1) {}"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report long parameter list if parameters with defaults should be ignored") {
            val config = TestConfig(mapOf(LongParameterList.IGNORE_DEFAULT_PARAMETERS to "true"))
            val rule = LongParameterList(config)
            val code = "fun long(a: Int, b: Int, c: Int = 2) {}"
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        it("reports too long parameter list for primary constructors") {
            val code = "class LongCtor(a: Int, b: Int)"
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(reportMessageForConstructor)
        }

        it("does not report short parameter list for primary constructors") {
            val code = "class LongCtor(a: Int)"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports too long parameter list for secondary constructors") {
            val code = "class LongCtor() { constructor(a: Int, b: Int) : this() }"
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(reportMessageForConstructor)
        }

        it("does not report short parameter list for secondary constructors") {
            val code = "class LongCtor() { constructor(a: Int) : this() }"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports long parameter list if custom threshold is set") {
            val config = TestConfig(mapOf(LongParameterList.CONSTRUCTOR_THRESHOLD to "1"))
            val rule = LongParameterList(config)
            val code = "class LongCtor(a: Int)"
            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        it("does not report long parameter list for constructors of data classes if asked") {
            val config = TestConfig(mapOf(
                LongParameterList.IGNORE_DATA_CLASSES to "true",
                LongParameterList.CONSTRUCTOR_THRESHOLD to "1"
            ))
            val rule = LongParameterList(config)
            val code = "data class Data(val a: Int)"
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        describe("constructors and functions with ignored annotations") {

            val config by memoized {
                TestConfig(mapOf(
                    LongParameterList.IGNORE_ANNOTATED to listOf("Generated", "kotlin.Deprecated", "kotlin.jvm.JvmName"),
                    LongParameterList.FUNCTION_THRESHOLD to 1,
                    LongParameterList.CONSTRUCTOR_THRESHOLD to 1
                ))
            }

            val rule by memoized { LongParameterList(config) }

            it("does not report long parameter list for constructors if file is annotated with ignored annotation") {
                val code = """
                    @file:kotlin.jvm.JvmName("test")
                    class Data(val a: Int)
                """
                assertThat(rule.compileAndLint(code)).isEmpty()
            }

            it("does not report long parameter list for functions if file is annotated with ignored annotation") {
                val code = """
                    @file:kotlin.jvm.JvmName("test")
                    class Data {
                        fun foo(a: Int) {} 
                    }
                """
                assertThat(rule.compileAndLint(code)).isEmpty()
            }

            it("does not report long parameter list for constructors if class is annotated with ignored annotation") {
                val code = """
                    annotation class Generated
                    @Generated class Data(val a: Int)
                """
                assertThat(rule.compileAndLint(code)).isEmpty()
            }

            it("does not report long parameter list for functions if class is annotated with ignored annotation") {
                val code = """
                    annotation class Generated
                    @Generated class Data { 
                        fun foo(a: Int) {} 
                    }
                """
                assertThat(rule.compileAndLint(code)).isEmpty()
            }

            it("does not report long parameter list for constructors if constructor is annotated with ignored annotation") {
                val code = "class Data @kotlin.Deprecated(message = \"\") constructor(val a: Int)"
                assertThat(rule.compileAndLint(code)).isEmpty()
            }

            it("does not report long parameter list for functions if function is annotated with ignored annotation") {
                val code = """class Data {
                    @kotlin.Deprecated(message = "") fun foo(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int) {} }
                """
                assertThat(rule.compileAndLint(code)).isEmpty()
            }
        }
    }
})
