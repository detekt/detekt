package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class AlsoCouldBeApplySpec {
    val subject = AlsoCouldBeApply(Config.Empty)

    @Test
    fun `does not report when no also is used`() {
        val code = """
            fun f(a: Int) {
                a.let {
                    it.plus(5)
                    it.minus(10)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports an also where only it is used in block`() {
        val code = """
            fun f(a: Int) {
                a.also {
                    it.plus(5)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `report is focused on also keyword`() {
        val code = """
            fun f(a: Int) {
                a.also {
                    it.plus(5)
                }
            }
        """.trimIndent()

        val findings = subject.lint(code)

        assertThat(findings).singleElement()
            .hasStartSourceLocation(2, 7)
            .hasEndSourceLocation(2, 11)
    }

    @Test
    fun `reports an also on nullable type`() {
        val code = """
            fun f(a: Int?) {
                a?.also {
                    it.plus(5)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports an also with lambda passed as Argument in parenthesis`() {
        val code = """
            fun f(a: Int?) {
                a?.also({
                    it.plus(5)
                })
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report if it is not used in also`() {
        val code = """
            fun f(a: Int?, b: Int) {
                a?.also {
                    b.plus(5)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report if it is renamed`() {
        val code = """
            fun f(x: Int, y: Int) {
                x.also { named ->
                    named.plus(5)
                    named.minus(y)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does report if it is on one line separated by semicolon`() {
        val code = """
            fun f(a: Int) {
                a.also { it.plus(5); it.minus(10) }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `detect violation in also nested in also`() {
        val code = """
            fun f(a: Int) {
                a.also { x -> x.also { it.plus(10) } }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report when all statements are not 'it'-started expressions`() {
        val code = """
            fun test(foo: Foo) {
                foo.also {
                    it.bar()
                    println(it)
                    it.baz()
                }
            }
            
            class Foo {
                fun bar() {}
                fun baz() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when no statements`() {
        val code = """
            fun test(foo: Foo) {
                foo.also {
                }
            }
            
            class Foo
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
