package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ImplicitUnitReturnTypeSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = ImplicitUnitReturnType(Config.Empty)

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

        val findings = ImplicitUnitReturnType(TestConfig("allowExplicitReturnType" to false))
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
