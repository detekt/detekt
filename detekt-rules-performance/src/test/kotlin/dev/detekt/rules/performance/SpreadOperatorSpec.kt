package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.test.KotlinEnvironmentContainer
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class SpreadOperatorSpec(val env: KotlinEnvironmentContainer) {

    private val subject = SpreadOperator(Config.empty)

    private val errorMessage = "Used in this way a spread operator causes a full copy of the array to" +
        " be created before calling a method. This may result in a performance penalty."

    @Test
    fun `reports when array copy required using named parameters`() {
        val code = """
            val xsArray = intArrayOf(1)
            fun foo(vararg xs: Int) {}
            val testVal = foo(xs = *xsArray)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).singleElement()
            .hasMessage(errorMessage)
    }

    @Test
    fun `reports when array copy required without using named parameters`() {
        val code = """
            val xsArray = intArrayOf(1)
            fun foo(vararg xs: Int) {}
            val testVal = foo(*xsArray)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).singleElement()
            .hasMessage(errorMessage)
    }

    @Test
    fun `doesn't report when using array constructor with spread operator`() {
        val code = """
            fun foo(vararg xs: Int) {}
            val testVal = foo(xs = *intArrayOf(1))
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `doesn't report when using array constructor with spread operator when varargs parameter comes first`() {
        val code = """
            fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
            val list = asList(-1, 0, *arrayOf(1, 2, 3), 4, stringValue = "5")
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `doesn't report when passing values directly`() {
        val code = """
            fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
            val list = asList(-1, 0, 1, 2, 3, 4, stringValue = "5")
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `doesn't report when function doesn't take a vararg parameter`() {
        val code = """
            fun test0(strs: Array<String>) {
                test(strs)
            }
            
            fun test(strs: Array<String>) {
                strs.forEach { println(it) }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `doesn't report with expression inside params`() {
        val code = """
            fun test0(strs: Array<String>) {
                test(2*2)
            }
            
            fun test(test : Int) {
                println(test)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `respects pass through of vararg parameter - #3145`() {
        val code = """
            fun b(vararg bla: Int) = Unit
            fun a(vararg bla: Int) {
                b(*bla)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports shadowed vararg declaration which may lead to array copy - #3145`() {
        val code = """
            fun b(vararg bla: String) = Unit
            
            fun a(vararg bla: Int) {
                val bla = arrayOf("")
                b(*bla)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }
}
