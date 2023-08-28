package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NestedScopeFunctionsSpec(private val env: KotlinCoreEnvironment) {

    private val defaultConfig = TestConfig(
        "allowedDepth" to 1,
        "functions" to listOf("kotlin.run", "kotlin.with")
    )
    private val subject = NestedScopeFunctions(defaultConfig)

    private lateinit var givenCode: String
    private lateinit var actual: List<Finding>

    @Test
    fun `should report nesting`() {
        givenCode = """
            fun f() {
                1.run {
                    1.run { }
                }
            }
        """.trimIndent()
        whenLintRuns()
        expectSourceLocation(3 to 11)
        expectFunctionInMsg("run")
    }

    @Test
    fun `should report mixed nesting`() {
        givenCode = """
            fun f() {
                1.run {
                    with(1) { }
                }
            }
        """.trimIndent()
        whenLintRuns()
        expectSourceLocation(3 to 9)
        expectFunctionInMsg("with")
    }

    @Test
    fun `should report when valid scope in between`() {
        givenCode = """
            fun f() {
                1.run {
                    "valid".apply {
                        with(1) { }
                    }
                }
            }
        """.trimIndent()
        whenLintRuns()
        expectSourceLocation(4 to 13)
    }

    @Test
    fun `should not report in nested function`() {
        givenCode = """
            fun f() {
                1.run { }
                fun f2() {
                    with(1) { }
                }
            }
        """.trimIndent()
        whenLintRuns()
        expectNoFindings()
    }

    @Test
    fun `should not report in neighboring scope functions`() {
        givenCode = """
            fun f() {
                1.run { }
                1.run { }
                with(1) {}
                with(1) {}
            }
        """.trimIndent()
        whenLintRuns()
        expectNoFindings()
    }

    @Test
    fun `should not report scope functions that have exactly the allowed depth`() {
        givenCode = """
            fun f() {
                1.run {
                }
            }
        """.trimIndent()
        whenLintRuns()
        expectNoFindings()
    }

    private fun whenLintRuns() {
        actual = subject.compileAndLintWithContext(env, givenCode)
    }

    private fun expectSourceLocation(location: Pair<Int, Int>) {
        assertThat(actual).hasStartSourceLocation(location.first, location.second)
    }

    private fun expectFunctionInMsg(scopeFunction: String) {
        val expected = "The scope function '$scopeFunction' is nested too deeply ('2'). " +
            "The maximum allowed depth is set to '1'."
        assertThat(actual[0]).hasMessage(expected)
    }

    private fun expectNoFindings() {
        assertThat(actual).describedAs("findings size").isEmpty()
    }
}
