package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val EXCLUDE_ANNOTATED_CLASSES = "excludeAnnotatedClasses"

@KotlinCoreEnvironmentTest
class UnnecessaryAbstractClassSpec(val env: KotlinCoreEnvironment) {
    val subject =
        UnnecessaryAbstractClass(TestConfig(mapOf(EXCLUDE_ANNOTATED_CLASSES to listOf("Deprecated"))))

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
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingMessage(findings, message)
        }

        @Nested
        inner class `reports completely-empty abstract classes` {
            @Test
            fun `case 1`() {
                val code = "abstract class A"
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
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
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
            }

            @Test
            fun `that inherits from another abstract class`() {
                val code = """
                    @Deprecated("We don't care about this first class")
                    abstract class A {
                        abstract val i: Int
                    }
                    abstract class B : A()
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an abstract class with a internal abstract member`() {
            val code = """
                abstract class A {
                    internal abstract fun f()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an abstract class with a protected abstract member`() {
            val code = """
                abstract class A {
                    protected abstract fun f()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `abstract classes with no abstract members` {

        val message = "An abstract class without an abstract member can be refactored to a concrete class."

        @Test
        fun `reports no abstract members in abstract class`() {
            val code = """
                abstract class A {
                    val i: Int = 0
                    fun f() {}
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingMessage(findings, message)
        }

        @Test
        fun `reports no abstract members in nested abstract class inside a concrete class`() {
            val code = """
                class Outer {
                    abstract class Inner {
                        fun f() {}
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingMessage(findings, message)
        }

        @Test
        fun `reports no abstract members in nested abstract class inside an interface`() {
            val code = """
                interface Inner {
                    abstract class A {
                        fun f() {}
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingMessage(findings, message)
        }

        @Test
        fun `reports no abstract members in an abstract class with just a constructor`() {
            val code = "abstract class A(val i: Int)"
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingMessage(findings, message)
        }

        @Test
        fun `reports no abstract members in an abstract class with a body and a constructor`() {
            val code = "abstract class A(val i: Int) {}"
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingMessage(findings, message)
        }

        @Test
        fun `reports an abstract class with no abstract member derived from a class with abstract members`() {
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
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingMessage(findings, message)
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
            """
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
            """
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report abstract classes with module annotation`() {
            val code = """
                @Deprecated("test")
                abstract class A {
                    abstract fun f()
                }
                
                @kotlin.Deprecated("test")
                abstract class B {
                    abstract fun f()
                } 
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report abstract classes with properties in the primary constructor`() {
            val code = """
                interface I {
                    fun test(): Int
                }
                abstract class Test(val x: Int) : I
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
}

private fun assertFindingMessage(findings: List<Finding>, message: String) {
    assertThat(findings).hasSize(1)
    assertThat(findings.first().message).isEqualTo(message)
}
