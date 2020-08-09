package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NamingConventionLengthSpec : Spek({

    val subject by memoized { NamingRules() }

    describe("NamingRules rule") {

        it("should not report underscore variable names") {
            val code = """
                fun getResult(): Pair<String, String> = TODO()
                fun function() {
                    val (_, status) = getResult()
                }
            """
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        it("should not report a variable with single letter name") {
            val code = "private val a = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        context("VariableMinLength rule with a custom minimum length") {

            val variableMinLength by memoized {
                VariableMinLength(TestConfig(mapOf(VariableMinLength.MINIMUM_VARIABLE_NAME_LENGTH to "2")))
            }

            it("reports a very short variable name") {
                val code = "private val a = 3"
                assertThat(variableMinLength.compileAndLint(code)).hasSize(1)
            }

            it("does not report a variable with only a single underscore") {
                val code = """
                    class C {
                        val prop: (Int) -> Unit = { _ -> Unit }
                }"""
                assertThat(variableMinLength.compileAndLint(code)).isEmpty()
            }
        }

        it("should not report a variable with 64 letters") {
            val code = "private val varThatIsExactly64LettersLongWhichYouMightNotWantToBelieveInLolz = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        it("should report a variable name that is too long") {
            val code = "private val thisVariableIsDefinitelyWayTooLongLongerThanEverythingAndShouldBeMuchShorter = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1)
        }

        it("should not report a variable name that is okay") {
            val code = "private val thisOneIsCool = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        it("should report a function name that is too short") {
            val code = "fun a() = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1)
        }

        it("should report a function name that is too long") {
            val code = "fun thisFunctionIsDefinitelyWayTooLongAndShouldBeMuchShorter() = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1)
        }

        it("should not report a function name that is okay") {
            val code = "fun three() = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        it("should not report a function name that begins with a backtick, capitals, and spaces") {
            val code = "fun `Hi bye`() = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }
    }
})
