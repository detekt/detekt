package dev.detekt.rules.standardlibrary

import dev.detekt.api.Config
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryChainedSafeCallSpec(private val env: KotlinEnvironmentContainer) {
    private val rule = UnnecessaryChainedSafeCall(Config.empty)

    @Test
    fun `reports non null assertion after one or more safe calls`() {
        val code = """
            class A(val b: B?)
            class B(val c: String?)

            fun test(a: A, nullableA: A?) {
                val shortChain = nullableA?.b!!
                val longChain = nullableA?.b?.c!!
            }
        """.trimIndent()

        val findings = rule.lintWithContext(env, code)

        assertThat(findings).hasSize(2)
        assertThat(findings).allMatch {
            it.message == "`!!` checks the result of a safe call chain. Check nullable values before later calls."
        }
    }

    @Test
    fun `reports Kotlin null check functions after a safe call chain`() {
        val code = """
            class A(val value: String?)

            fun test(a: A) {
                requireNotNull(a.value?.trim())
                checkNotNull(a.value?.trim())
            }
        """.trimIndent()

        val findings = rule.lintWithContext(env, code)

        assertThat(findings.map { it.message }).containsExactly(
            "`requireNotNull` checks the result of a safe call chain. Check nullable values before later calls.",
            "`checkNotNull` checks the result of a safe call chain. Check nullable values before later calls.",
        )
    }

    @Test
    fun `does not report null checks before later calls`() {
        val code = """
            class A(val b: B?)
            class B(val value: String)

            fun test(a: A) {
                val assertedValue = a.b!!.value
                val requiredValue = requireNotNull(a.b).value
                val checkedValue = checkNotNull(a.b).value
            }
        """.trimIndent()

        assertThat(rule.lintWithContext(env, code)).isEmpty()
    }
}
