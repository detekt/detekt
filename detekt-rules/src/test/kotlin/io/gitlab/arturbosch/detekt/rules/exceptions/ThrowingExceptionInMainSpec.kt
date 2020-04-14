package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ThrowingExceptionInMainSpec : Spek({
    val subject by memoized { ThrowingExceptionInMain() }

    describe("ThrowingExceptionInMain rule") {

        it("reports a runnable main function which throws an exception") {
            val code = """
                fun main(args: Array<String>) { throw IllegalArgumentException() }
                fun main() { throw IllegalArgumentException() }
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports runnable main functions with @JvmStatic annotation which throw an exception") {
            val code = """
                class A {
                    companion object {
                        @JvmStatic
                        fun main(args: Array<String>) { throw IllegalArgumentException() }
                    }
                }
                
                class B {
                    companion object {
                        @kotlin.jvm.JvmStatic
                        fun main() { throw IllegalArgumentException() }
                    }
                }
                
                object O {
                    @JvmStatic
                    fun main(args: Array<String>) { throw IllegalArgumentException() }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(3)
        }

        it("does not report top level main functions with a wrong signature") {
            val code = """
                private fun main(args: Array<String>) { throw IllegalArgumentException() }
                private fun main() { throw IllegalArgumentException() }
                fun mai() { throw IllegalArgumentException() }
                fun main(args: String) { throw IllegalArgumentException() }
                fun main(args: Array<String>, i: Int) { throw IllegalArgumentException() }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report top level main functions which throw no exception") {
            val code = """
                fun main(args: Array<String>) { }
                fun main() { }
                fun mai() { }
                fun main(args: String) { }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report top level main functions with expression body which throw no exception") {
            val code = """
                fun main(args: Array<String>) = ""
                fun main() = Unit
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report main functions with no @JvmStatic annotation inside a class") {
            val code = """
            class A {
                fun main(args: Array<String>) { throw IllegalArgumentException() }
                
                companion object {
                    fun main(args: Array<String>) { throw IllegalArgumentException() }
                }
            }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
