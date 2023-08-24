package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class AbstractClassCanBeInterfaceSpec(val env: KotlinCoreEnvironment) {
    val subject = AbstractClassCanBeInterface()

    @Nested
    inner class `abstract classes with no concrete members` {
        val message = "An abstract class without a concrete member can be refactored to an interface."

        @Test
        fun `reports an abstract class with no concrete member`() {
            val code = """
                abstract class A {
                    abstract val i: Int
                    abstract fun f()
                    public abstract fun f2()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingMessage(findings, message)
            assertThat(findings).hasStartSourceLocation(1, 16)
        }

        @Nested
        inner class `reports completely-empty abstract classes` {
            @Test
            fun `case 1`() {
                val code = "abstract class A"
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
                assertThat(findings).hasStartSourceLocation(1, 16)
            }

            @Test
            fun `case 2`() {
                val code = "abstract class A()"
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
            }

            @Test
            fun `case 3`() {
                val code = "abstract class A {}"
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
            }

            @Test
            fun `case 4`() {
                val code = "abstract class A() {}"
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
            }

            @Test
            fun `that inherits from an interface`() {
                val code = """
                    interface A {
                        val i: Int
                    }
                    abstract class B : A
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
            }

            @Test
            fun `that inherits from another abstract class`() {
                val code = """
                    abstract class A {
                        // Added non-abstract member to ensure this A class does not get reported
                        val nonAbstractMember: Int = 2

                        abstract val i: Int
                    }
                    abstract class B : A()
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report abstract class that inherits from an abstract class and an interface in that order`() {
                val code = """
                    interface I
                    
                    abstract class A {
                        // Added non-abstract member to ensure this A class does not get reported
                        val nonAbstractMember: Int = 2

                        abstract val i: Int
                    }
                    abstract class B: A(), I
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does not report abstract class that inherits from an interface and an abstract class in that order`() {
                val code = """
                    interface I
                    
                    abstract class A {
                        // Added non-abstract member to ensure this A class does not get reported
                        val nonAbstractMember: Int = 2

                        abstract val i: Int
                    }
                    abstract class B: I, A()
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        @Test
        fun `does not report an abstract class with concrete members derived from a base class`() {
            val code = """
                abstract class A {
                    abstract fun f()
                    val i: Int = 0
                }
                
                abstract class B : A() {
                    abstract fun g()
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an abstract class with a internal abstract member`() {
            val code = """
                abstract class A {
                    internal abstract fun f()
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an abstract class with a protected abstract member`() {
            val code = """
                abstract class A {
                    protected abstract fun f()
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `abstract classes with no abstract members` {

        val message = "An abstract class without an abstract member can be refactored to a concrete class."

        @Test
        fun `does not report no abstract members in abstract class`() {
            val code = """
                abstract class A {
                    val i: Int = 0
                    fun f() {}
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report no abstract members in nested abstract class inside a concrete class`() {
            val code = """
                class Outer {
                    abstract class Inner {
                        fun f() {}
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report no abstract members in nested abstract class inside an interface`() {
            val code = """
                interface Inner {
                    abstract class A {
                        fun f() {}
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report no abstract members in an abstract class with just a constructor`() {
            val code = "abstract class A(val i: Int)"
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report no abstract members in an abstract class with a body and a constructor`() {
            val code = "abstract class A(val i: Int) {}"
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report no abstract members in an abstract class with just a constructor parameter`() {
            val code = "abstract class A(i: Int)"
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report an abstract class with no abstract member derived from a class with abstract members`() {
            val code = """
                abstract class Base {
                    abstract val i: Int
                    abstract fun f()
                    fun f1() {}
                }
                
                abstract class Sub : Base() {
                    override val i: Int
                        get() = 1
                
                    override fun f() {}
                
                    fun g() {}
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `abstract classes with members` {

        @Test
        fun `does not report an abstract class with members and an abstract class derived from it`() {
            val code = """
                abstract class A {
                    abstract val i: Int
                    fun f() {}
                }
                
                abstract class B : A() {
                    fun g() {}
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an abstract class with a constructor and an abstract class derived from it`() {
            val code = """
                abstract class A(val i: Int) {
                    abstract fun f()
                }
                
                abstract class B : A(0) {
                    fun g() {}
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an abstract class with a function derived from an interface`() {
            val code = """
                abstract class A : Interface {
                    fun g() {}
                }
                
                interface Interface {
                    fun f()
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report abstract classes with properties in the primary constructor`() {
            val code = """
                interface I {
                    fun test(): Int
                }
                abstract class Test(val x: Int) : I
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
}

private fun assertFindingMessage(findings: List<Finding>, message: String) {
    assertThat(findings).hasSize(1)
    assertThat(findings.first()).hasMessage(message)
}
