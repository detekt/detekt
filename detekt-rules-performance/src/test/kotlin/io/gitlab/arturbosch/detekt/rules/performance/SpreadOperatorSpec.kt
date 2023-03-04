package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class SpreadOperatorSpec(val env: KotlinCoreEnvironment) {

    val subject = SpreadOperator()

    /**
     * This rule has different behaviour depending on whether type resolution is enabled in detekt or not. The two
     * `context` blocks are there to test behaviour when type resolution is enabled and type resolution is disabled
     * as different warning messages are shown in each case.
     */
    @Nested
    inner class `with type resolution` {

        val typeResolutionEnabledMessage = "Used in this way a spread operator causes a full copy of the array to" +
            " be created before calling a method. This may result in a performance penalty."

        @Test
        fun `reports when array copy required using named parameters`() {
            val code = """
                val xsArray = intArrayOf(1)
                fun foo(vararg xs: Int) {}
                val testVal = foo(xs = *xsArray)
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo(typeResolutionEnabledMessage)
        }

        @Test
        fun `reports when array copy required without using named parameters`() {
            val code = """
                val xsArray = intArrayOf(1)
                fun foo(vararg xs: Int) {}
                val testVal = foo(*xsArray)
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo(typeResolutionEnabledMessage)
        }

        @Test
        fun `doesn't report when using array constructor with spread operator`() {
            val code = """
                fun foo(vararg xs: Int) {}
                val testVal = foo(xs = *intArrayOf(1))
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `doesn't report when using array constructor with spread operator when varargs parameter comes first`() {
            val code = """
                fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
                val list = asList(-1, 0, *arrayOf(1, 2, 3), 4, stringValue = "5")
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `doesn't report when passing values directly`() {
            val code = """
                fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
                val list = asList(-1, 0, 1, 2, 3, 4, stringValue = "5")
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `respects pass through of vararg parameter - #3145`() {
            val code = """
                fun b(vararg bla: Int) = Unit
                fun a(vararg bla: Int) {
                    b(*bla)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `without type resolution` {

        val typeResolutionDisabledMessage = "In most cases using a spread operator causes a full copy of the " +
            "array to be created before calling a method. This may result in a performance penalty."

        @Test
        fun `reports when array copy required using named parameters`() {
            val code = """
                val xsArray = intArrayOf(1)
                fun foo(vararg xs: Int) {}
                val testVal = foo(xs = *xsArray)
            """.trimIndent()
            val actual = subject.compileAndLint(code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo(typeResolutionDisabledMessage)
        }

        @Test
        fun `reports when array copy required without using named parameters`() {
            val code = """
                val xsArray = intArrayOf(1)
                fun foo(vararg xs: Int) {}
                val testVal = foo(*xsArray)
            """.trimIndent()
            val actual = subject.compileAndLint(code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo(typeResolutionDisabledMessage)
        }

        @Test
        fun `doesn't report when using array constructor with spread operator`() {
            val code = """
                fun foo(vararg xs: Int) {}
                val testVal = foo(xs = *intArrayOf(1))
            """.trimIndent()
            val actual = subject.compileAndLint(code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo(typeResolutionDisabledMessage)
        }

        @Test
        fun `doesn't report when using array constructor with spread operator when varargs parameter comes first`() {
            val code = """
                fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
                val list = asList(-1, 0, *arrayOf(1, 2, 3), 4, stringValue = "5")
            """.trimIndent()
            val actual = subject.compileAndLint(code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo(typeResolutionDisabledMessage)
        }

        @Test
        fun `doesn't report when passing values directly`() {
            val code = """
                fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
                val list = asList(-1, 0, 1, 2, 3, 4, stringValue = "5")
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
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
            assertThat(subject.compileAndLint(code)).isEmpty()
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
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `respects pass through of vararg parameter - #3145`() {
            val code = """
                fun b(vararg bla: Int) = Unit
                fun a(vararg bla: Int) {
                    b(*bla)
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report shadowed vararg declaration, we except this false negative here - #3145`() {
            val code = """
                fun b(vararg bla: String) = Unit
                
                fun a(vararg bla: Int) {
                    val bla = arrayOf("")
                    b(*bla)
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
