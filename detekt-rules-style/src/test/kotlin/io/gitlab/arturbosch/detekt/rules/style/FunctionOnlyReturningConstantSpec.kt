package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private const val IGNORE_OVERRIDABLE_FUNCTION = "ignoreOverridableFunction"
private const val IGNORE_ACTUAL_FUNCTION = "ignoreActualFunction"
private const val EXCLUDED_FUNCTIONS = "excludedFunctions"
private const val EXCLUDE_ANNOTATED_FUNCTION = "excludeAnnotatedFunction"

class FunctionOnlyReturningConstantSpec : Spek({
    val subject by memoized { FunctionOnlyReturningConstant() }

    describe("FunctionOnlyReturningConstant rule - positive cases") {

        val path = Case.FunctionReturningConstantPositive.path()

        it("reports functions which return constants") {
            assertThat(subject.lint(path)).hasSize(6)
        }

        it("reports overridden functions which return constants") {
            val config = TestConfig(mapOf(IGNORE_OVERRIDABLE_FUNCTION to "false"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(path)).hasSize(9)
        }

        val actualFunctionCode = """
            actual class ActualFunctionReturningConstant {
                actual fun f() = 1
            }
        """

        it("does not report actual functions which return constants") {
            assertThat(subject.lint(actualFunctionCode)).isEmpty()
        }

        it("reports actual functions which return constants") {
            val config = TestConfig(mapOf(IGNORE_ACTUAL_FUNCTION to "false"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(actualFunctionCode)).hasSize(1)
        }

        it("does not report excluded function which returns a constant") {
            val code = "fun f() = 1"
            val config = TestConfig(mapOf(EXCLUDED_FUNCTIONS to "f"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        val code = """
            import kotlin.SinceKotlin
            class Test {
                @SinceKotlin("1.0.0")
                fun someIgnoredFun(): String {
                    return "I am a constant"
                }
            }
        """

        listOf(
            TestConfig(mapOf(EXCLUDE_ANNOTATED_FUNCTION to "kotlin.SinceKotlin")),
            TestConfig(mapOf(EXCLUDE_ANNOTATED_FUNCTION to listOf("kotlin.SinceKotlin")))
        ).forEach { config ->
            it("does not report excluded annotated function which returns a constant") {

                val rule = FunctionOnlyReturningConstant(config)
                assertThat(rule.compileAndLint(code)).isEmpty()
            }
        }
    }

    describe("FunctionOnlyReturningConstant rule - negative cases") {

        it("does not report functions which do not return constants") {
            val path = Case.FunctionReturningConstantNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
