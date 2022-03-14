package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NamingConventionLengthSpec {

    @Nested
    inner class `NamingRules rule` {

        @Test
        fun `should not report underscore variable names`() {
            val subject = NamingRules()
            val code = """
                fun getResult(): Pair<String, String> = TODO()
                fun function() {
                    val (_, status) = getResult()
                }
            """
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `should not report a variable with single letter name`() {
            val subject = NamingRules()
            val code = "private val a = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Nested
        inner class `VariableMinLength rule with a custom minimum length` {

            val variableMinLength =
                VariableMinLength(TestConfig(mapOf(VariableMinLength.MINIMUM_VARIABLE_NAME_LENGTH to "2")))

            @Test
            fun `reports a very short variable name`() {
                val code = "private val a = 3"
                assertThat(variableMinLength.compileAndLint(code)).hasSize(1)
            }

            @Test
            fun `does not report a variable with only a single underscore`() {
                val code = """
                    class C {
                        val prop: (Int) -> Unit = { _ -> Unit }
                }
                """
                assertThat(variableMinLength.compileAndLint(code)).isEmpty()
            }
        }

        @Test
        fun `should not report a variable with 64 letters`() {
            val subject = NamingRules()
            val code = "private val varThatIsExactly64LettersLongWhichYouMightNotWantToBelieveInLolz = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `should report a variable name that is too long`() {
            val subject = NamingRules()
            val code = "private val thisVariableIsDefinitelyWayTooLongLongerThanEverythingAndShouldBeMuchShorter = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1)
        }

        @Test
        fun `should not report a variable name that is okay`() {
            val subject = NamingRules()
            val code = "private val thisOneIsCool = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `should report a function name that is too short`() {
            val subject = NamingRules()
            val code = "fun a() = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1)
        }

        @Test
        fun `should report a function name that is too long`() {
            val subject = NamingRules()
            val code = "fun thisFunctionIsDefinitelyWayTooLongAndShouldBeMuchShorter() = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1)
        }

        @Test
        fun `should not report a function name that is okay`() {
            val subject = NamingRules()
            val code = "fun three() = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `should report a function name that begins with a backtick, capitals, and spaces`() {
            val subject = NamingRules()
            val code = "fun `Hi bye`() = 3"
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1)
        }
    }
}
