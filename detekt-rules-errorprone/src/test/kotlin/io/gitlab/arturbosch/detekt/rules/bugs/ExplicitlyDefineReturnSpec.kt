package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@KotlinCoreEnvironmentTest
class ExplicitlyDefineReturnSpec(private val env: KotlinCoreEnvironment) {
    private val subject = ExplicitlyDefineReturn()

    @ParameterizedTest(
        name = "when allowOmitUnit is {0}, {1} implicit Unit return type",
    )
    @CsvSource(value = ["true,'does not report'", "false,'does report'"])
    fun `Given function return is Unit`(
        allowOmitUnit: Boolean,
        @Suppress("UnusedParameter", "UNUSED_PARAMETER") isAllowedMsgString: String,
    ) {
        val code = """
            fun errorProneUnit() = println("Hello Unit")
            fun errorProneUnitWithParam(param: String) = param.run { println(this) }
            fun String.errorProneUnitWithReceiver() = run { println(this) }
        """.trimIndent()

        val subject = ExplicitlyDefineReturn(
            TestConfig(
                ALLOW_OMIT_UNIT to allowOmitUnit
            )
        )
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(if (allowOmitUnit) 0 else 3)
    }

    @Test
    fun `does report when function return implicit type which is NOT unit`() {
        val code = """
            fun errorProneList() = listOf("Hello Unit")
            fun errorProneListWithParam(param: String) = mutableListOf<Int>().run { this.size }
            fun String.errorProneListWithReceiver() = run { listOf("a", "t", "u", "l") }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(3)
    }

    @Test
    fun `does report implicit generic return type`() {
        val code = """
            fun <M> M.identity() = this
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasMessage(
            "`identity` has the implicit return type." +
                " Prefer specify the return type explicitly"
        )
    }

    @Test
    fun `does report implicit generic return type which is NOT Unit when calling a generic fun`() {
        val code = """
            fun <M> M.identity(): M = this
            fun intReturnType() = 5.identity()
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report implicit generic return type which IS Unit when calling a generic fun`() {
        val code = """
            fun <M> M.identity(): M = this
            fun unitReturnType() = Unit.identity()
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(0)
    }

    @Test
    fun `does not report implicit Unit return type by default`() {
        val code = """fun safeUnitReturn() = println("Hello Unit")"""

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @ParameterizedTest(
        name = "does not report for implicit Unit return type for block function when allowOmitUnit is {0}",
    )
    @ValueSource(booleans = [true, false])
    fun `does not report for block statements with Unit return`(allowOmitUnit: Boolean) {
        val code = """
            fun blockUnitReturn() {
                println("Hello Unit")
            }
        """.trimIndent()
        val subject = ExplicitlyDefineReturn(
            TestConfig(
                ALLOW_OMIT_UNIT to allowOmitUnit
            )
        )
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report for block statements with List return`() {
        val code = """
            fun blockUnitReturn(): List<String> {
                return listOf("a", "b")
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @ParameterizedTest(
        name = "does not report for implicit type Unit expression when allowOmitUnit is {0}",
    )
    @ValueSource(booleans = [true, false])
    fun `does not report for Unit expression`(allowOmitUnit: Boolean) {
        val code = """
            fun foo() = Unit
        """.trimIndent()
        val subject = ExplicitlyDefineReturn(
            TestConfig(
                ALLOW_OMIT_UNIT to allowOmitUnit
            )
        )
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report single nested function`() {
        val code = """
            fun a() {
                fun b(): Int = 1.apply { 
                     fun b() = 5
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report correct count for nested implicit return function`() {
        val code = """
            fun a() {
                fun b(): Int = 1.apply { 
                     fun b() = 2.apply {
                         fun c(): Int = 3.apply {
                             fun d() = 4
                         }
                     }
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(2)
    }

    companion object {
        private const val ALLOW_OMIT_UNIT = "allowOmitUnit"
    }
}
