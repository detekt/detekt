package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.regex.PatternSyntaxException

class EmptyCodeSpec : Spek({

    val regexTestingCode = """
            fun f() {
                try {
                } catch (foo: Exception) {
                }
            }"""

    describe("EmptyCatchBlock rule") {

        it("findsEmptyCatch") {
            test { EmptyCatchBlock(Config.empty) }
        }

        it("findsEmptyNestedCatch") {
            val code = """
            fun f() {
                try {
                } catch (ignore: Exception) {
                    try {
                    } catch (e: Exception) {
                    }
                }
            }"""
            assertThat(EmptyCatchBlock(Config.empty).compileAndLint(code)).hasSize(1)
        }

        it("doesNotReportIgnoredOrExpectedException") {
            val code = """
            fun f() {
                try {
                } catch (ignore: IllegalArgumentException) {
                } catch (expected: Exception) {
                }
            }"""
            assertThat(EmptyCatchBlock(Config.empty).compileAndLint(code)).isEmpty()
        }

        it("doesNotReportEmptyCatchWithConfig") {
            val code = """
            fun f() {
                try {
                } catch (foo: Exception) {
                }
            }"""
            val config = TestConfig(mapOf(EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "foo"))
            assertThat(EmptyCatchBlock(config).compileAndLint(code)).isEmpty()
        }

        it("findsEmptyFinally") {
            test { EmptyFinallyBlock(Config.empty) }
        }

        it("findsEmptyIf") {
            test { EmptyIfBlock(Config.empty) }
        }

        it("findsEmptyElse") {
            test { EmptyElseBlock(Config.empty) }
        }

        it("findsEmptyFor") {
            test { EmptyForBlock(Config.empty) }
        }

        it("findsEmptyWhile") {
            test { EmptyWhileBlock(Config.empty) }
        }

        it("findsEmptyDoWhile") {
            test { EmptyDoWhileBlock(Config.empty) }
        }

        it("findsEmptyFun") {
            test { EmptyFunctionBlock(Config.empty) }
        }

        it("findsEmptyClass") {
            test { EmptyClassBlock(Config.empty) }
        }

        it("findsEmptyTry") {
            test { EmptyTryBlock(Config.empty) }
        }

        it("findsEmptyWhen") {
            test { EmptyWhenBlock(Config.empty) }
        }

        it("findsEmptyInit") {
            test { EmptyInitBlock(Config.empty) }
        }

        it("findsOneEmptySecondaryConstructor") {
            test { EmptySecondaryConstructor(Config.empty) }
        }

        it("findsEmptyDefaultConstructor") {
            val rule = EmptyDefaultConstructor(Config.empty)
            val file = compileForTest(resourceAsPath("EmptyDefaultConstructorPositive.kt"))
            assertThat(rule.lint(file)).hasSize(2)
        }

        it("doesNotFindEmptyDefaultConstructor") {
            val rule = EmptyDefaultConstructor(Config.empty)
            val file = compileForTest(resourceAsPath("EmptyDefaultConstructorNegative.kt"))
            assertThat(rule.lint(file)).isEmpty()
        }

        it("doesNotFailWithInvalidRegexWhenDisabled") {
            val configValues = mapOf("active" to "false",
                    EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "*foo")
            val config = TestConfig(configValues)
            assertThat(EmptyCatchBlock(config).compileAndLint(regexTestingCode)).isEmpty()
        }

        it("doesFailWithInvalidRegex") {
            val configValues = mapOf(EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "*foo")
            val config = TestConfig(configValues)
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                EmptyCatchBlock(config).compileAndLint(regexTestingCode)
            }
        }
    }
})

private fun test(block: () -> Rule) {
    val rule = block()
    rule.lint(compileForTest(resourceAsPath("Empty.kt")))
    assertThat(rule.findings).hasSize(1)
}
