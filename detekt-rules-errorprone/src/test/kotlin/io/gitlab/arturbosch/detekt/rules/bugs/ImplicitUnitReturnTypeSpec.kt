package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ImplicitUnitReturnTypeSpec(private val env: KotlinCoreEnvironment) {
    private val subject = ImplicitUnitReturnType(Config.empty)

    @Nested
    inner class `Functions returning Unit via expression statements` {

        @Test
        fun `reports implicit Unit return types`() {
            val code = """
                fun errorProneUnit() = println("Hello Unit")
                fun errorProneUnitWithParam(param: String) = param.run { println(this) }
                fun String.errorProneUnitWithReceiver() = run { println(this) }
            """

            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(3)
        }

        @Test
        fun `does not report explicit Unit return type by default`() {
            val code = """fun safeUnitReturn(): Unit = println("Hello Unit")"""

            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports explicit Unit return type if configured`() {
            val code = """fun safeButStillReported(): Unit = println("Hello Unit")"""

            val findings = ImplicitUnitReturnType(TestConfig("allowExplicitReturnType" to "false"))
                .compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report for block statements`() {
            val code = """
                fun blockUnitReturn() { 
                    println("Hello Unit")
                }
            """

            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report for Unit expression`() {
            val code = """
                fun foo() = Unit
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
