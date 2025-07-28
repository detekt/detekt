package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val EXCLUDE_ANNOTATED_CLASSES = "excludeAnnotatedClasses"

@KotlinCoreEnvironmentTest
class AbstractClassCanBeConcreteClassSpec(val env: KotlinEnvironmentContainer) {
    val subject = AbstractClassCanBeConcreteClass(TestConfig(EXCLUDE_ANNOTATED_CLASSES to listOf("Deprecated")))

    @Nested
    inner class `abstract classes with no concrete members` {
        val message = "An abstract class without a concrete member can be refactored to an interface."

        @Test
        fun `does not report an abstract class with no concrete member`() {
            val code = """
                abstract class A {
                    abstract val i: Int
                    abstract fun f()
                    public abstract fun f2()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Nested
        inner class `does not report completely-empty abstract classes` {
            @Test
            fun `case 1`() {
                val code = "abstract class A"
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `case 2`() {
                val code = "abstract class A()"
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `case 3`() {
                val code = "abstract class A {}"
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `case 4`() {
                val code = "abstract class A() {}"
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `that inherits from an interface`() {
                val code = """
                    interface A {
                        val i: Int
                    }
                    abstract class B : A
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `that inherits from another abstract class`() {
                val code = """
                    @Deprecated("We don't care about this first class")
                    abstract class A {
                        abstract val i: Int
                    }
                    abstract class B : A()
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report abstract class that inherits from an abstract class and an interface in that order`() {
                val code = """
                    interface I
                    
                    @Deprecated("We don't care about this first class")
                    abstract class A {
                        abstract val i: Int
                    }
                    abstract class B: A(), I
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does not report abstract class that inherits from an interface and an abstract class in that order`() {
                val code = """
                    interface I
                    
                    @Deprecated("We don't care about this first class")
                    abstract class A {
                        abstract val i: Int
                    }
                    abstract class B: I, A()
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an abstract class with a internal abstract member`() {
            val code = """
                abstract class A {
                    internal abstract fun f()
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an abstract class with a protected abstract member`() {
            val code = """
                abstract class A {
                    protected abstract fun f()
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            val findings = subject.lintWithContext(env, code)
            assertFindingMessage(findings, message)
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
            val findings = subject.lintWithContext(env, code)
            assertFindingMessage(findings, message)
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
            val findings = subject.lintWithContext(env, code)
            assertFindingMessage(findings, message)
        }

        @Test
        fun `does not report no abstract members in an abstract class with just a constructor`() {
            val code = "abstract class A(val i: Int)"
            val findings = subject.lintWithContext(env, code)
            assertFindingMessage(findings, message)
            assertThat(findings).hasStartSourceLocation(1, 16)
        }

        @Test
        fun `does not report no abstract members in an abstract class with a body and a constructor`() {
            val code = "abstract class A(val i: Int) {}"
            val findings = subject.lintWithContext(env, code)
            assertFindingMessage(findings, message)
        }

        @Test
        fun `does not report no abstract members in an abstract class with just a constructor parameter`() {
            val code = "abstract class A(i: Int)"
            val findings = subject.lintWithContext(env, code)
            assertFindingMessage(findings, message)
            assertThat(findings).hasStartSourceLocation(1, 16)
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
            val findings = subject.lintWithContext(env, code)
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
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report abstract classes with properties in the primary constructor`() {
            val code = """
                interface I {
                    fun test(): Int
                }
                abstract class Test(val x: Int) : I
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
}

private fun assertFindingMessage(findings: List<Finding>, message: String) {
    assertThat(findings).hasSize(1)
    assertThat(findings.first()).hasMessage(message)
}
