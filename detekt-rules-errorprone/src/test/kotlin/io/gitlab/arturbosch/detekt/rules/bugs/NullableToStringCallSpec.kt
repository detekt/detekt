package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object NullableToStringCallSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { NullableToStringCall() }

    describe("NullableToString rule") {
        it("reports when a nullable toString is explicitly called") {
            val code = """
                fun test(a: Any?) {
                    println(a.toString())
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo("This call 'a.toString()' may return the string \"null\".")
        }

        it("reports when a nullable toString is implicitly called in a string template") {
            val code = """
                fun test(a: Any?) {
                    println("${'$'}a")
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo("This call '\$a' may return the string \"null\".")
        }

        it("reports when a nullable toString is implicitly called in curly braces in a string template") {
            val code = """
                fun test(a: Any?) {
                    println("${'$'}{a}")
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo("This call '\${a}' may return the string \"null\".")
        }

        it("reports when a nullable toString is implicitly called in a raw string template") {
            val code = """
                fun test(a: Any?) {
                    println(${'"'}""${'$'}a""${'"'})
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
            assertThat(actual.first().message).isEqualTo("This call '\$a' may return the string \"null\".")
        }

        it("does not report when a nullable toString is not called") {
            val code = """
                fun test(a: Any?) {
                    println(a?.toString())
                    println("${'$'}{a ?: "-"}")
                }
                fun test2(a: Any) {
                    println(a.toString())
                    println("${'$'}a")
                    println("${'$'}{a}")
                    println(${'"'}""${'$'}a""${'"'})
                }
                fun test3(a: Any?) {
                    if (a != null) {
                        println(a.toString())
                        println("${'$'}a")
                        println("${'$'}{a}")
                        println(${'"'}""${'$'}a""${'"'})
                    }
                }
                fun test4(a: Any?) {
                    requireNotNull(a)
                    println(a.toString())
                    println("${'$'}a")
                    println("${'$'}{a}")
                    println(${'"'}""${'$'}a""${'"'})
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }
    }
})
