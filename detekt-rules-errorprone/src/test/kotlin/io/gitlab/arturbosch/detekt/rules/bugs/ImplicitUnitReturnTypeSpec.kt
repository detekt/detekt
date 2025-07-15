package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ImplicitUnitReturnTypeSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = ImplicitUnitReturnType(Config.empty)

    @Test
    fun `reports implicit Unit return types`() {
        val code = """
            fun errorProneUnit() = println("Hello Unit")
            fun errorProneUnitWithParam(param: String) = param.run { println(this) }
            fun String.errorProneUnitWithReceiver() = run { println(this) }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).hasSize(3)
    }

    @Test
    fun `does not report explicit Unit return type by default`() {
        val code = """fun safeUnitReturn(): Unit = println("Hello Unit")"""

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports explicit Unit return type if configured`() {
        val code = """fun safeButStillReported(): Unit = println("Hello Unit")"""

        val findings = ImplicitUnitReturnType(TestConfig("allowExplicitReturnType" to "false"))
            .lintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report for block statements`() {
        val code = """
            fun blockUnitReturn() {
                println("Hello Unit")
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report for Unit expression`() {
        val code = """
            fun foo() = Unit
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
