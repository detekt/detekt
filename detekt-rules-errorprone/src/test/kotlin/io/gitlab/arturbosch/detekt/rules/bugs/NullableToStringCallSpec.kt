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

        it("reports when a nullable toString is explicitly called and the expression is qualified/call expression") {
            val code = """
                data class Foo(val a: Any?) {
                    fun bar(): Int? = null
                }
                fun baz(): Long? = null
                
                fun test(foo: Foo) {
                    val x = foo.a.toString()
                    val y = foo.bar().toString()
                    val z = baz().toString()
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(3)
            assertThat(actual[0].message).isEqualTo("This call 'foo.a.toString()' may return the string \"null\".")
            assertThat(actual[1].message).isEqualTo("This call 'foo.bar().toString()' may return the string \"null\".")
            assertThat(actual[2].message).isEqualTo("This call 'baz().toString()' may return the string \"null\".")
        }

        it("reports when a nullable toString is implicitly called and the expression is qualified/call expression") {
            val code = """
                data class Foo(val a: Any?) {
                    fun bar(): Int? = null
                }
                fun baz(): Long? = null
                
                fun test(foo: Foo) {
                    val x = "${'$'}{foo.a}"
                    val y = "${'$'}{foo.bar()}"
                    val z = "${'$'}{baz()}"
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(3)
            assertThat(actual[0].message).isEqualTo("This call '\${foo.a}' may return the string \"null\".")
            assertThat(actual[1].message).isEqualTo("This call '\${foo.bar()}' may return the string \"null\".")
            assertThat(actual[2].message).isEqualTo("This call '\${baz()}' may return the string \"null\".")
        }

        it("reports when a nullable toString is implicitly called and the expression is safe qualified expression") {
            val code = """
                data class Foo(val a: Any)
                
                fun test(foo: Foo?) {
                    val y = "${'$'}{foo?.a}"
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
            assertThat(actual[0].message).isEqualTo("This call '\${foo?.a}' may return the string \"null\".")
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

                data class Foo(val a: Any?)
                fun test5(foo: Foo) {
                    if (foo.a == null) return
                    val x = foo.a.toString()
                    val y = "${'$'}{foo.a}"
                }                

                data class Bar(val a: Any)
                fun test6(bar: Bar?) {
                    if (bar == null) return
                    val x = bar?.a.toString()
                    val y = "${'$'}{bar?.a}"
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }
    }
})
