package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class AlsoCouldBeApplySpec {
    val subject = AlsoCouldBeApply(Config.empty)

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
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports an also in init of class`() {
        val code = """
            fun f(a: Int) {
                a.also { 
                    it.plus(5)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
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
        assertThat(subject.compileAndLint(code)).hasSize(1)
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
        assertThat(subject.compileAndLint(code)).hasSize(1)
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
        assertThat(subject.compileAndLint(code)).isEmpty()
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
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
