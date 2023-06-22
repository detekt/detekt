package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MethodOverloadingSpec {
    private val defaultAllowedOverloads = 2
    private val defaultConfig = TestConfig("allowedOverloads" to defaultAllowedOverloads)

    val subject = MethodOverloading(defaultConfig)

    @Nested
    inner class `several overloaded methods` {

        @Test
        fun `reports overloaded methods which exceed the threshold`() {
            val code = """
                class Test {
                    fun x() {}
                    fun x(i: Int) {}
                    fun x(i: Int, j: Int) {}
                }
            """.trimIndent()
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("The method 'x' is overloaded 3 times.")
        }

        @Test
        fun `reports overloaded top level methods which exceed the threshold`() {
            val code = """
                fun x() {}
                fun x(i: Int) {}
                fun x(i: Int, j: Int) {}
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report overloaded methods which do not exceed the threshold`() {
            subject.compileAndLint(
                """
                    class Test {
                        fun x() { }
                        fun x(i: Int) { }
                    }
                """.trimIndent()
            )
            assertThat(subject.findings.size).isZero()
        }
    }

    @Nested
    inner class `several overloaded extensions methods` {

        @Test
        fun `does not report extension methods with a different receiver`() {
            subject.compileAndLint(
                """
                    fun Boolean.foo() {}
                    fun Int.foo() {}
                    fun Long.foo() {}
                """.trimIndent()
            )
            assertThat(subject.findings.size).isZero()
        }

        @Test
        fun `reports extension methods with the same receiver`() {
            subject.compileAndLint(
                """
                    fun Int.foo() {}
                    fun Int.foo(i: Int) {}
                    fun Int.foo(i: String) {}
                """.trimIndent()
            )
            assertThat(subject.findings.size).isEqualTo(1)
        }
    }

    @Nested
    inner class `several nested overloaded methods` {

        @Test
        fun `reports nested overloaded methods which exceed the threshold`() {
            val code = """
                class Outer {
                    internal class Inner {
                        fun f() {}
                        fun f(i: Int) {}
                        fun f(i: Int, j: Int) {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report nested overloaded methods which do not exceed the threshold`() {
            val code = """
                class Outer {
                
                    fun f() {}
                
                    internal class Inner {
                        fun f(i: Int) {}
                        fun f(i: Int, j: Int) {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `several overloaded methods inside objects` {

        @Test
        fun `reports overloaded methods inside an object which exceed the threshold`() {
            val code = """
                object Test {
                    fun f() {}
                    fun f(i: Int) {}
                    fun f(i: Int, j: Int) {}
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report overloaded methods inside an object which do not exceed the threshold`() {
            val code = """
                object Test {
                    fun f() {}
                    fun f(i: Int) {}
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports overloaded methods inside a companion object which exceed the threshold`() {
            val code = """
                class Test {
                    companion object {
                        fun f() {}
                        fun f(i: Int) {}
                        fun f(i: Int, j: Int) {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report overloaded methods in a companion object that do not exceed the threshold`() {
            val code = """
                class Test {
                    companion object {
                        fun f() {}
                        fun f(i: Int) {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report overloaded methods in classes or objects that do not exceed the threshold`() {
            val code = """
                class Test {
                
                    fun f() {}
                
                    companion object {
                        fun f() {}
                        fun f(i: Int) {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports overloaded methods inside an anonymous object expression`() {
            val code = """
                class A {
                    fun f() {
                        object : Runnable {
                            override fun run() {}
                            fun run(i: Int) {}
                            fun run(i: Int, j: Int) {}
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `several overloaded methods inside enum classes` {

        @Test
        fun `does not report overridden methods inside enum entries`() {
            val code = """
                enum class Test {
                    E1 {
                        override fun f() {}
                    },
                    E2 {
                        override fun f() {}
                    },
                    E3 {
                        override fun f() {}
                    };
                
                    abstract fun f()
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports overloaded methods in enum entry`() {
            val code = """
                enum class Test {
                    E {
                        fun f(i: Int) {}
                        fun f(i: Int, j: Int) {}
                    };
                    fun f() {}
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports overloaded methods in enum class`() {
            val code = """
                enum class Test {
                    E;
                
                    fun f() {}
                    fun f(i: Int) {}
                    fun f(i: Int, j: Int) {}
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }

    @Test
    fun `does not report a class without a body`() {
        val code = "class A"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report a method that has exactly the allowed overloads`() {
        val code = """
             class Test {              
                 fun f(i: Int) {}
                 fun f(i: Int, j: Int) {}
             }
        """.trimIndent()
        val actual = subject.compileAndLint(code)

        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report overloaded local functions`() {
        val code = """
            fun top() {
                fun f() {}
                fun f(i: Int) {}
                fun f(i: Int, j: Int) {}
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
