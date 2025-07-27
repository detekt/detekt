package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnusedUnaryOperatorSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UnusedUnaryOperator(Config.empty)

    @Test
    fun `unused plus operator`() {
        val code = """
            fun test() {
                val x = 1 + 2
                    + 3
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("This '+ 3' is not used")
        assertThat(findings).hasStartSourceLocation(3, 9)
    }

    @Test
    fun `unused minus operator`() {
        val code = """
            fun test() {
                val x = 1 + 2
                    - 3
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("This '- 3' is not used")
        assertThat(findings).hasStartSourceLocation(3, 9)
    }

    @Test
    fun `unused plus operator in binary expression`() {
        val code = """
            fun test() {
                val x = 1 + 2
                    + 3 + 4 + 5
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("This '+ 3 + 4 + 5' is not used")
    }

    @Test
    fun `used plus operator`() {
        val code = """
            fun test() {
                val x = (1 + 2
                    + 3 + 4 + 5)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `used minus operator`() {
        val code = """
            fun test() {
                val x = -1
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `used as return value`() {
        val code = """
            fun test(): Int {
                return -1
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `used as value argument`() {
        val code = """
            fun foo(x: Int) {}
            fun test() {
                foo(x = -1)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `used as annotation value argument`() {
        val code = """
            annotation class Ann(val x: Int)
            @Ann(x = -1)
            val y = 2
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `overloaded unary operator`() {
        val code = """
            data class Foo(val x: Int)
            operator fun Foo.plus(other: Foo) = Foo(this.x + other.x)
            operator fun Foo.unaryMinus() = Foo(-x)
            fun test() {
                val p = Foo(1) + Foo(2)
                    - Foo(3)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `var assignment by if expression`() {
        val code = """
            fun test(b: Boolean) {
                var x = 0
                x = if (b) {
                    -1
                } else {
                    1
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
