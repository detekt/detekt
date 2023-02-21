package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class OptionalUnitSpec(val env: KotlinCoreEnvironment) {

    val subject = OptionalUnit(Config.empty)

    @Test
    fun `should report when a function has an explicit Unit return type with context`() {
        val code = """
            fun foo(): Unit { }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report when a function has a non-unit body expression`() {
        val code = """
            fun foo() = String
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Nested
    inner class `several functions which return Unit` {

        val code = """
            fun returnsUnit1(): Unit {
                fun returnsUnitNested(): Unit {
                    return Unit
                }
                return Unit
            }

            fun returnsUnit2() = Unit
        """.trimIndent()
        lateinit var findings: List<Finding>

        @BeforeEach
        fun beforeEachTest() {
            findings = subject.compileAndLint(code)
        }

        @Test
        fun `should report functions returning Unit`() {
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `should report the correct violation message`() {
            findings.forEach {
                assertThat(it.message).endsWith(
                    " defines a return type of Unit. This is unnecessary and can safely be removed."
                )
            }
        }
    }

    @Nested
    inner class `an overridden function which returns Unit` {

        @Test
        fun `should not report Unit return type in overridden function`() {
            val code = """
                interface I {
                    fun returnsUnit()
                }
                class C : I {
                    override fun returnsUnit() = Unit
                }
            """.trimIndent()
            val findings = subject.compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `several lone Unit statements` {

        val code = """
            fun returnsNothing() {
                Unit
                val i: (Int) -> Unit = { _ -> Unit }
                if (true) {
                    Unit
                }
            }

            class A {
                init {
                    Unit
                }
            }
        """.trimIndent()
        lateinit var findings: List<Finding>

        @BeforeEach
        fun beforeEachTest() {
            findings = subject.compileAndLint(code)
        }

        @Test
        fun `should report lone Unit statement`() {
            assertThat(findings).hasSize(4)
        }

        @Test
        fun `should report the correct violation message`() {
            findings.forEach {
                assertThat(it.message).isEqualTo("A single Unit expression is unnecessary and can safely be removed.")
            }
        }
    }

    @Nested
    inner class `several Unit references` {

        @Test
        fun `should not report Unit reference`() {
            val findings = subject.compileAndLint(
                """
                fun returnsNothing(u: Unit, us: () -> String) {
                    val u1 = u is Unit
                    val u2: Unit = Unit
                    val Unit = 1
                    Unit.equals(null)
                    val i: (Int) -> Unit = { _ -> }
                }
                """.trimIndent()
            )
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a default interface implementation` {
        @Test
        fun `should report Unit as part of default interface implementations`() {
            val code = """
                interface Foo {
                    fun method(i: Int) = Unit
                }
            """.trimIndent()
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `last statement in block - #2452` {
        @Test
        fun `unused as an expression`() {
            val code = """
                fun test(i: Int, b: Boolean) {
                    when (i) {
                        0 -> println(1)
                        else -> {
                            if (b) {
                                println(2)
                            }
                            Unit
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `used as an expression and the previous expression is not a Unit type`() {
            val code = """
                fun <T> T.foo() {
                    println(this)
                }
                
                fun test(i: Int, b: Boolean) {
                    when (i) {
                        0 -> println(1)
                        else -> {
                            1
                            Unit
                        }
                    }.foo()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `used as an expression and the previous expression cannot be used as a value`() {
            val code = """
                fun <T> T.foo() {
                    println(this)
                }
                
                fun test(i: Int, j: Int) {
                    when (i) {
                        0 -> println(1)
                        else -> {
                            if (j == 1) {
                                println(2)
                            } else if (j == 2) {
                                println(3)
                            } else if (j == 3) {
                                println(4)
                            }
                            Unit
                        }
                    }.foo()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `used as an expression and the previous expression cannot be used as a value 2`() {
            val code = """
                fun <T> T.foo() {
                    println(this)
                }
                
                fun test(i: Int, j: Int) {
                    when (i) {
                        0 -> println(1)
                        else -> {
                            when (j) {
                                1 -> println(2)
                                2 -> println(3)
                                3 -> println(4)
                            }
                            Unit
                        }
                    }.foo()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `used as an expression and the previous expression can be used as a value`() {
            val code = """
                fun <T> T.foo() {
                    println(this)
                }
                
                fun test(i: Int, j: Int) {
                    when (i) {
                        0 -> println(1)
                        else -> {
                            if (j == 1) {
                                println(2)
                            } else if (j == 2) {
                                println(3)
                            } else if (j == 3) {
                                println(4)
                            } else {
                                println(5)
                            }
                            Unit
                        }
                    }.foo()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `used as an expression and the previous expression can be used as a value 2`() {
            val code = """
                fun <T> T.foo() {
                    println(this)
                }
                
                fun test(i: Int, j: Int) {
                    when (i) {
                        0 -> println(1)
                        else -> {
                            when (j) {
                                1 -> println(2)
                                2 -> println(3)
                                3 -> println(4)
                                else -> println(5)
                            }
                            Unit
                        }
                    }.foo()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `used as an expression and the previous expression can be used as a value 3`() {
            val code = """
                fun <T> T.foo() {
                    println(this)
                }
                
                enum class E { A, B }
                
                fun test(i: Int, e: E) {
                    when (i) {
                        0 -> println(1)
                        else -> {
                            when (e) {
                                E.A -> println(1)
                                E.B -> println(2)
                            }
                            Unit
                        }
                    }.foo()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `another object is used as the last expression`() {
            val code = """
                fun foo() {
                    String
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `function initializers` {
        @Test
        fun `should not report when function initializer is Nothing`() {
            val code = """
                fun test(): Unit = throw UnsupportedOperationException()
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report when the function initializer requires a type`() {
            val code = """
                fun <T> foo(block: (List<T>) -> Unit): T {
                    val list = listOf<T>()
                    block(list)
                    return list.first()
                }

                fun doFoo(): Unit = foo {}
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report on function initializers when there is no context`() {
            val code = """
                fun test(): Unit = throw UnsupportedOperationException()
            """.trimIndent()
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report when the function initializer takes in the type Nothing`() {
            val code = """
                fun <T> foo(block: (List<T>) -> Unit): T {
                    val list = listOf<T>()
                    block(list)
                    return list.first()
                }

                fun doFoo(): Unit = foo<Nothing> {}
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report when the function initializer does not provide a different type`() {
            val code = """
                fun foo() {}
                
                fun doFoo(): Unit = foo()
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }
}
