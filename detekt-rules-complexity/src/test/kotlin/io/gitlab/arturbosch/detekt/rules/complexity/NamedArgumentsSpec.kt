package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NamedArgumentsSpec(val env: KotlinCoreEnvironment) {
    val defaultThreshold = 2
    val defaultConfig = TestConfig(mapOf("threshold" to defaultThreshold))
    val subject = NamedArguments(defaultConfig)

    @Test
    fun `invocation with more than 2 parameters should throw error`() {
        val code = """
            fun sum(a: Int, b:Int, c:Int) {
                println(a + b + c)
            }
            fun call() {
                sum(1, 2, 3)
            }
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Function invocation with more than 2 parameters should not throw error if named`() {
        val code = """
            fun sum(a: Int, b:Int, c:Int) {
                println(a + b + c)
            }
            fun call() {
                sum(a = 1, b = 2, c = 3)
            }
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(0)
    }

    @Test
    fun `invocation with more than 2 parameters should throw error if even one is not named`() {
        val code = """
            fun sum(a: Int, b:Int, c:Int) {
                println(a + b + c)
            }
            fun call() {
                sum(1, b = 2, c = 3)
            }
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `invocation with less than 3 parameters should not throw error`() {
        val code = """
            fun sum(a: Int, b:Int) {
                println(a + b)
            }
            fun call() {
                sum(1, 2)
            }
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(0)
    }

    @Test
    fun `invocation with less than 3 named parameters should not throw error`() {
        val code = """
            fun sum(a: Int, b:Int) {
                println(a + b)
            }
            fun call() {
                sum(a = 1, b = 2)
            }
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(0)
    }

    @Test
    fun `constructor invocation with more than 3 non-named parameters should throw error`() {
        val code = """
            class C(val a: Int, val b:Int, val c:Int)
            
            val obj = C(1, 2, 3)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `constructor invocation with more than 3 named parameters should not throw error`() {
        val code = """
            class C(val a: Int, val b:Int, val c:Int)
            
            val obj = C(a = 1, b = 2, c= 3)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(0)
    }

    @Test
    fun `constructor invocation with less than 3 non-named parameters should not throw error`() {
        val code = """
            class C(val a: Int, val b:Int)
            
            val obj = C(1, 2)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(0)
    }

    @Test
    fun `java method invocation should not be flagged`() {
        val code = """
            import java.time.LocalDateTime
            
            fun test() {
                LocalDateTime.of(2020, 3, 13, 14, 0, 0)
            }
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(0)
    }

    @Test
    fun `invocation with varargs should not be flagged`() {
        val code = """
            fun foo(vararg i: Int) {}
            fun bar(a: Int, b: Int, c: Int, vararg s: String) {}
            fun test() {
                foo(1, 2, 3, 4, 5)
                bar(1, 2, 3, "a")
            }
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(0)
    }

    @Test
    fun `invocation with spread operator should be flagged`() {
        val code = """
            fun bar(a: Int, b: Int, c: Int, vararg s: String) {}
            fun test() {
                bar(1, 2, 3, *arrayOf("a"))
            }
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Nested
    inner class `lambda argument` {
        @Test
        fun `inner lambda argument`() {
            val code = """
            fun foo(a: Int, b: Int, c: Int, block: ((Int) -> Int)) {}
            
            fun test() {
                foo(a = 1, b = 2, c = 3, { it })
            }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `outer lambda argument`() {
            val code = """
            fun foo(a: Int, b: Int, c: Int, block: ((Int) -> Int)) {}
            
            fun test() {
                foo(a = 1, b = 2, c = 3) { it }
            }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(0)
        }

        @Test
        fun `unnamed argument and outer argument`() {
            val code = """
            fun foo(a: Int, b: Int, c: Int, block: ((Int) -> Int)) {}
            
            fun test() {
                foo(a = 1, b = 2, 3) { it }
            }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not count lambda argument`() {
            val code = """
            fun test(n: Int) {
                require(n == 2) { "N is not 2" }
            }
        """
            val subject = NamedArguments(TestConfig(mapOf("threshold" to 1)))
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(0)
        }
    }

    @Nested
    inner class IgnoreArgumentsMatchingNames {
        @Nested
        inner class `ignoreArgumentsMatchingNames is true` {
            val subject =
                NamedArguments(TestConfig(mapOf("threshold" to 2, "ignoreArgumentsMatchingNames" to true)))

            @Test
            fun `all arguments are the same as the parameter names`() {
                val code = """
                    fun foo(a: Int, b: Int, c: Int) {}
                    fun bar(a: Int, b: Int, c: Int) {
                        foo(a, b, c)
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(0)
            }

            @Test
            fun `some arguments are not the same as the parameter names`() {
                val code = """
                    fun foo(a: Int, b: Int, c: Int) {}
                    fun bar(a: Int, b: Int, c: Int) {
                        foo(a, c, b)
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `all arguments are the same as the parameter names and have a this receiver`() {
                val code = """
                    class Baz {
                        private var b: Int = 42
                        fun foo(a: Int, b: Int, c: Int) {}
                        fun bar(a: Int, c: Int) {
                            foo(a, this.b, c)
                        }
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `all arguments are the same as the parameter names and have a it receiver`() {
                val code = """
                    data class Baz(val b: Int)
                    fun foo(a: Int, b: Int, c: Int) {}
                    fun bar(a: Int, c: Int, baz: Baz?) {
                        baz?.let { foo(a, it.b, c) }
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class `ignoreArgumentsMatchingNames is false` {
            @Test
            fun `all arguments are the same as parameter names`() {
                val code = """
                    fun foo(a: Int, b: Int, c: Int) {}
                    fun bar(a: Int, b: Int, c: Int) {
                        foo(a, b, c)
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }
    }
}
