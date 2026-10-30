package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@KotlinCoreEnvironmentTest
class NullableBooleanCheckSpec(val env: KotlinEnvironmentContainer) {
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

        val findings = NullableBooleanCheck(TestConfig("onlyConditionals" to false))
            .lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage(
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

        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage(
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

        assertThat(subject.lintWithContext(env, code)).isEmpty()
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

        assertThat(subject.lintWithContext(env, code)).isEmpty()
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

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report non-elvis binary expression`() {
        val code = """
            import kotlin.random.Random
            
            fun foo(): Boolean {
                return Random.nextBoolean() || false
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in statement by default`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo(): Boolean {
                return nullableBoolean() ?: true
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in property initializer by default`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo() {
                val isFlag = nullableBoolean() ?: true
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in expression-body function by default`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo(): Boolean = nullableBoolean() ?: true
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in constructor default by default`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            class Foo(flag: Boolean = nullableBoolean() ?: true)
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in init block by default`() {
        val code = """
            class Foo(value: Boolean?) {
                init {
                    println(value ?: true)
                }
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in if then-branch by default`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo(): Boolean {
                return if (true) nullableBoolean() ?: true else false
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in while body by default`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo(): Boolean {
                while (true) return nullableBoolean() ?: true
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in when branch by default`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo(x: Int): Boolean {
                return when (x) {
                    1 -> nullableBoolean() ?: true
                    else -> false
                }
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in subjectless when branch by default`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo(): Boolean {
                return when {
                    true -> nullableBoolean() ?: true
                    else -> false
                }
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports elvis in while condition`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo() {
                while (nullableBoolean() ?: true) println("foo")
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis in do-while condition`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo() {
                do { println("foo") } while (nullableBoolean() ?: true)
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis in when subject`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo() {
                when (nullableBoolean() ?: true) {
                    true -> println("foo")
                    false -> println("bar")
                }
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis in when condition without subject`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo() {
                when {
                    nullableBoolean() ?: true -> println("foo")
                }
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis nested in compound if condition`() {
        val code = """
            import kotlin.random.Random

            fun nullableBoolean(): Boolean? = true.takeIf { Random.nextBoolean() }

            fun foo(other: Boolean) {
                if ((nullableBoolean() ?: true) && other) println("foo")
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report non-nullable boolean elvis`() {
        val code = """
            fun foo(value: Boolean): Boolean {
                return value ?: true
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
