package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@KotlinCoreEnvironmentTest
class NullableBooleanCheckSpec(val env: KotlinCoreEnvironment) {
    val subject = NullableBooleanCheck(Config.empty)

    /**
     * The recommended replacement string for `?: [fallback]`.
     */
    private fun replacementForElvis(fallback: Boolean): String = if (fallback) "!= false" else "== true"

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `reports elvis in statement`(bool: Boolean) {
        val code = """
            import kotlin.random.Random
            
            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }
            
            fun foo(): Boolean {
                return nullableBoolean() ?: $bool
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).first().extracting { it.message }.isEqualTo(
            "The nullable boolean check `nullableBoolean() ?: $bool` should use " +
                "`${replacementForElvis(bool)}` rather than `?: $bool`"
        )
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `reports elvis in if condition`(bool: Boolean) {
        val code = """
            import kotlin.random.Random
            
            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }
            
            fun foo() {
                if (nullableBoolean() ?: $bool) println("foo")
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).first().extracting { it.message }.isEqualTo(
            "The nullable boolean check `nullableBoolean() ?: $bool` should use " +
                "`${replacementForElvis(bool)}` rather than `?: $bool`"
        )
    }

    @Test
    fun `does not report for non-constant fallback`() {
        val code = """
            import kotlin.random.Random
            
            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }
            
            fun foo(): Boolean {
                return nullableBoolean() ?: Random.nextBoolean()
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `does not report elvis for non-boolean statement with boolean default`(bool: Boolean) {
        val code = """
            import kotlin.random.Random
            
            fun nullableAny(): Any? = Unit.takeIf { Random.nextBoolean() }
            
            fun foo(): Any {
                return nullableAny() ?: $bool
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report non-boolean elvis`() {
        val code = """
            import kotlin.random.Random
            
            fun nullableInt(): Int? = 42.takeIf { Random.nextBoolean() }
            
            fun foo(): Int {
                return nullableInt() ?: 0
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report non-elvis binary expression`() {
        val code = """
            import kotlin.random.Random
            
            fun foo(): Boolean {
                return Random.nextBoolean() || false
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
