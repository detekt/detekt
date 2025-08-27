package dev.detekt.rules.complexity

import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NamedArgumentsSpec(val env: KotlinEnvironmentContainer) {
    private val defaultAllowedArguments = 2
    private val defaultConfig = TestConfig("allowedArguments" to defaultAllowedArguments)
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
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
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
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
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
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
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
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
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
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `constructor invocation with more than 3 non-named parameters should throw error`() {
        val code = """
            class C(val a: Int, val b:Int, val c:Int)
            
            val obj = C(1, 2, 3)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `constructor invocation with more than 3 named parameters should not throw error`() {
        val code = """
            class C(val a: Int, val b:Int, val c:Int)
            
            val obj = C(a = 1, b = 2, c= 3)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `constructor invocation with less than 3 non-named parameters should not throw error`() {
        val code = """
            class C(val a: Int, val b:Int)
            
            val obj = C(1, 2)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `java method invocation should not be flagged`() {
        val code = """
            import java.time.LocalDateTime
            
            fun test() {
                LocalDateTime.of(2020, 3, 13, 14, 0, 0)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
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
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `invocation with spread varargs should not be flagged`() {
        val code = """
            fun varargFun(str: String, vararg nums: Int) {}
            fun call() {
                val nums1 = intArrayOf(1, 2, 3)
                val nums2 = intArrayOf(4, 5, 6)
                val nums3 = intArrayOf(7, 8, 9)
                varargFun("a", *nums1, *nums2, *nums3)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `invocation with spread operator should be flagged`() {
        val code = """
            fun bar(a: Int, b: Int, c: Int, vararg s: String) {}
            fun test() {
                bar(1, 2, 3, *arrayOf("a"))
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
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
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `outer lambda argument`() {
            val code = """
                fun foo(a: Int, b: Int, c: Int, block: ((Int) -> Int)) {}
                
                fun test() {
                    foo(a = 1, b = 2, c = 3) { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `unnamed argument and outer argument`() {
            val code = """
                fun foo(a: Int, b: Int, c: Int, block: ((Int) -> Int)) {}
                
                fun test() {
                    foo(a = 1, b = 2, 3) { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not count lambda argument`() {
            val code = """
                fun test(n: Int) {
                    require(n == 2) { "N is not 2" }
                }
            """.trimIndent()
            val subject = NamedArguments(TestConfig("threshold" to 1))
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class IgnoreArgumentsMatchingNames {
        @Nested
        inner class `ignoreArgumentsMatchingNames is true` {
            val subject =
                NamedArguments(TestConfig("allowedArguments" to 2, "ignoreArgumentsMatchingNames" to true))

            @Test
            fun `all arguments are the same as the parameter names`() {
                val code = """
                    fun foo(a: Int, b: Int, c: Int) {}
                    fun bar(a: Int, b: Int, c: Int) {
                        foo(a, b, c)
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `some arguments are not the same as the parameter names`() {
                val code = """
                    fun foo(a: Int, b: Int, c: Int) {}
                    fun bar(a: Int, b: Int, c: Int) {
                        foo(a, c, b)
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
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
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
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
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
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
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }
    }
}
