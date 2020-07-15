package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SpreadOperatorSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { SpreadOperator() }

    describe("SpreadOperator rule") {
        /** This rule has different behaviour depending on whether type resolution is enabled in detekt or not. The two
         * `context` blocks are there to test behaviour when type resolution is enabled and type resolution is disabled
         * as different warning messages are shown in each case.
         */
        context("with type resolution") {

            val typeResolutionEnabledMessage = "Used in this way a spread operator causes a full copy of the array to" +
                " be created before calling a method which has a very high performance penalty."

            it("reports when array copy required using named parameters") {
                val code = """
                    val xsArray = intArrayOf(1)
                    fun foo(vararg xs: Int) {}
                    val testVal = foo(xs = *xsArray)
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().message).isEqualTo(typeResolutionEnabledMessage)
            }
            it("reports when array copy required without using named parameters") {
                val code = """
                    val xsArray = intArrayOf(1)
                    fun foo(vararg xs: Int) {}
                    val testVal = foo(*xsArray)
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().message).isEqualTo(typeResolutionEnabledMessage)
            }
            it("doesn't report when using array constructor with spread operator") {
                val code = """
                    fun foo(vararg xs: Int) {}
                    val testVal = foo(xs = *intArrayOf(1))
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("doesn't report when using array constructor with spread operator when varargs parameter comes first") {
                val code = """
                    fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
                    val list = asList(-1, 0, *arrayOf(1, 2, 3), 4, stringValue = "5")
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("doesn't report when passing values directly") {
                val code = """
                    fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
                    val list = asList(-1, 0, 1, 2, 3, 4, stringValue = "5")
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("doesn't report when function doesn't take a vararg parameter") {
                val code = """
                fun test0(strs: Array<String>) {
                    test(strs)
                }

                fun test(strs: Array<String>) {
                    strs.forEach { println(it) }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("doesn't report with expression inside params") {
                val code = """
                fun test0(strs: Array<String>) {
                    test(2*2)
                }

                fun test(test : Int) {
                    println(test)
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("without type resolution") {

            val typeResolutionDisabledMessage = "In most cases using a spread operator causes a full copy of the " +
                "array to be created before calling a method which has a very high performance penalty."

            it("reports when array copy required using named parameters") {
                val code = """
                    val xsArray = intArrayOf(1)
                    fun foo(vararg xs: Int) {}
                    val testVal = foo(xs = *xsArray)
                """
                val actual = subject.compileAndLint(code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().message).isEqualTo(typeResolutionDisabledMessage)
            }
            it("reports when array copy required without using named parameters") {
                val code = """
                    val xsArray = intArrayOf(1)
                    fun foo(vararg xs: Int) {}
                    val testVal = foo(*xsArray)
                """
                val actual = subject.compileAndLint(code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().message).isEqualTo(typeResolutionDisabledMessage)
            }
            it("doesn't report when using array constructor with spread operator") {
                val code = """
                    fun foo(vararg xs: Int) {}
                    val testVal = foo(xs = *intArrayOf(1))
                """
                val actual = subject.compileAndLint(code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().message).isEqualTo(typeResolutionDisabledMessage)
            }

            it("doesn't report when using array constructor with spread operator when varargs parameter comes first") {
                val code = """
                    fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
                    val list = asList(-1, 0, *arrayOf(1, 2, 3), 4, stringValue = "5")
                """
                val actual = subject.compileAndLint(code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().message).isEqualTo(typeResolutionDisabledMessage)
            }

            it("doesn't report when passing values directly") {
                val code = """
                    fun <T> asList(vararg ts: T, stringValue: String): List<Int> = listOf(1,2,3)
                    val list = asList(-1, 0, 1, 2, 3, 4, stringValue = "5")
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("doesn't report when function doesn't take a vararg parameter") {
                val code = """
                fun test0(strs: Array<String>) {
                    test(strs)
                }

                fun test(strs: Array<String>) {
                    strs.forEach { println(it) }
                }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("doesn't report with expression inside params") {
                val code = """
                fun test0(strs: Array<String>) {
                    test(2*2)
                }

                fun test(test : Int) {
                    println(test)
                }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }
    }
})
