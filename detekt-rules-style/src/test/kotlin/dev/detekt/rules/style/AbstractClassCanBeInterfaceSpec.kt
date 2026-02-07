package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.rules.style.AbstractClassCanBeInterface.Companion.NO_CONCRETE_MEMBER
import dev.detekt.rules.style.AbstractClassCanBeInterface.Companion.SEALED_NO_CONCRETE_MEMBER
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class AbstractClassCanBeInterfaceSpec(val env: KotlinEnvironmentContainer) {
    val subject = AbstractClassCanBeInterface(Config.empty)

    @Nested
    inner class `abstract classes with no concrete members` {
        @Test
        fun `reports an abstract class with no concrete member`() {
            val code = """
                abstract class A {
                    abstract val i: Int
                    abstract fun f()
                    public abstract fun f2()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage(NO_CONCRETE_MEMBER)
                .hasStartSourceLocation(1, 16)
        }

        @Nested
        inner class `reports completely-empty abstract classes` {
            @Test
            fun `case 1`() {
                val code = "abstract class A"
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).singleElement()
                    .hasMessage(NO_CONCRETE_MEMBER)
                    .hasStartSourceLocation(1, 16)
            }

            @Test
            fun `case 2`() {
                val code = "abstract class A()"
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).singleElement()
                    .hasMessage(NO_CONCRETE_MEMBER)
            }

            @Test
            fun `case 3`() {
                val code = "abstract class A {}"
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).singleElement()
                    .hasMessage(NO_CONCRETE_MEMBER)
            }

            @Test
            fun `case 4`() {
                val code = "abstract class A() {}"
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).singleElement()
                    .hasMessage(NO_CONCRETE_MEMBER)
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
                assertThat(findings).singleElement()
                    .hasMessage(NO_CONCRETE_MEMBER)
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
                assertThat(subject.lintWithContext(env, code)).isEmpty()
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
                val findings = subject.lintWithContext(env, code)
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

        @Test
        fun `does not report an abstract class containing an internal nested class`() {
            val code = """
                abstract class A {
                    abstract val x: Int
                    internal class InternalImpl {
                        val data: Int = 42
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `abstract classes with no abstract members` {
        @Test
        fun `does not report no abstract members in abstract class`() {
            val code = """
                abstract class A {
                    val i: Int = 0
                    fun f() {}
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
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
            val findings = subject.lintWithContext(env, code)
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
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report no abstract members in an abstract class with just a constructor`() {
            val code = "abstract class A(val i: Int)"
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report no abstract members in an abstract class with a body and a constructor`() {
            val code = "abstract class A(val i: Int) {}"
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report no abstract members in an abstract class with just a constructor parameter`() {
            val code = "abstract class A(i: Int)"
            val findings = subject.lintWithContext(env, code)
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
            val findings = subject.lintWithContext(env, code)
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

    @Nested
    inner class SealedClasses {
        @Test
        fun `report a sealed class with no abstract members`() {
            val code = """
                sealed class Result {
                    data class Success(val data: Int) : Result()
                    data class Failure(val reason: String) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings)
                .singleElement()
                .hasMessage(SEALED_NO_CONCRETE_MEMBER)
        }

        @Test
        fun `don't report a sealed class with constructor params`() {
            val code = """
                sealed class Result(val value: Int) {
                    data class Success(val data: Int) : Result(123)
                    data class Failure(val reason: String) : Result(456)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `report a sealed class with only abstract methods`() {
            val code = """
                sealed class Result {
                    abstract fun x()
                    data class Success(val data: Int) : Result() {
                        override fun x() = println("success!")
                    }
                    data class Failure(val reason: String) : Result() {
                        override fun x() = println("failure...")
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings)
                .singleElement()
                .hasMessage(SEALED_NO_CONCRETE_MEMBER)
        }

        @Test
        fun `report a sealed class with open methods`() {
            val code = """
                sealed class Result {
                    open fun x() = println("default")
                    data class Success(val data: Int) : Result() {
                        override fun x() = println("success!")
                    }
                    data class Failure(val reason: String) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings)
                .singleElement()
                .hasMessage(SEALED_NO_CONCRETE_MEMBER)
        }

        @Test
        fun `don't report a sealed class with final methods`() {
            val code = """
                sealed class Result {
                    fun x() = println("final!") 
                    data class Success(val data: Int) : Result()
                    data class Failure(val reason: String) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `report a sealed class with only abstract properties`() {
            val code = """
                sealed class Result {
                    abstract val x: Int
                    data class Success(val data: Int, override val x: Int) : Result()
                    data class Failure(val reason: String) : Result() {
                        override val x = 789
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings)
                .singleElement()
                .hasMessage(SEALED_NO_CONCRETE_MEMBER)
        }

        @Test
        fun `report a sealed class with non-const open properties`() {
            val code = """
                fun computeSomething(): Int = 456 * 789

                sealed class Result {
                    open val x: Int = computeSomething()
                    data class Success(val data: Int, override val x: Int) : Result()
                    data class Failure(val reason: String) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings)
                .singleElement()
                .hasMessage(SEALED_NO_CONCRETE_MEMBER)
        }

        @Test
        fun `don't report a sealed class with open literal const properties`() {
            val code = """
                sealed class Result {
                    open val code: Int = 404
                    data class Success(val data: Int, override val code: Int) : Result()
                    data class Failure(val reason: String) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `don't report a sealed class with open const properties`() {
            val code = """
                const val DEFAULT_CODE = 404

                sealed class Result {
                    open val code: Int = DEFAULT_CODE
                    data class Success(val data: Int, override val code: Int) : Result()
                    data class Failure(val reason: String) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `don't report a sealed class with mixed property types`() {
            val code = """
                const val CONST_VALUE = 123
                fun computeSomething(): Int = 456 * 789

                sealed class Result {
                    abstract val abstractVal: Int
                    open val openConstVal: Int = CONST_VALUE
                    open val openNonConstVal: Int = computeSomething()
                    val finalVal: Int = 789

                    data class Success(val data: Int, override val abstractVal: Int) : Result()

                    data class Failure(val reason: String) : Result() {
                        override val abstractVal: Int = 1234
                        override var openConstVal: Int = 56789
                            set(value) {
                                field = value
                                computeSomething()
                            }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `don't report a sealed class with only final properties`() {
            val code = """
                sealed class Result {
                    val x: Int = 123
                    data class Success(val data: Int) : Result()
                    data class Failure(val reason: String) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `don't report a sealed interface`() {
            val code = """
                sealed interface Result {
                    val x: Int
                    data class Success(val data: Int, override val x: Int) : Result
                    data class Failure(val reason: String) : Result {
                        override val x = 789
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        // This would otherwise be flagged by this rule, but Kotlin doesn't allow internal classes declared within an
        // interface
        @Test
        fun `don't report a sealed class containing an internal implementation class`() {
            val code = """
                sealed class Result {
                    internal data class Success(val data: Int) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `don't report a sealed class with abstract properties and an internal nested class`() {
            val code = """
                sealed class Result {
                    abstract val x: Int
                    internal data class Success(val data: Int, override val x: Int) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `don't report a sealed class with multiple nested classes including an internal one`() {
            val code = """
                sealed class Result {
                    abstract val x: Int
                    data class Success(val data: Int, override val x: Int) : Result()
                    internal data class Loading(override val x: Int) : Result()
                    data class Failure(val reason: String, override val x: Int) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `report a sealed class with only public nested classes`() {
            val code = """
                sealed class Result {
                    abstract val x: Int
                    data class Success(val data: Int, override val x: Int) : Result()
                    data class Failure(val reason: String, override val x: Int) : Result()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings)
                .singleElement()
                .hasMessage(SEALED_NO_CONCRETE_MEMBER)
        }
    }
}
