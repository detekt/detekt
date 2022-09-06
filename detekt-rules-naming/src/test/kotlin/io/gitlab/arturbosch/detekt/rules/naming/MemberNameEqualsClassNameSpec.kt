package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val IGNORE_OVERRIDDEN = "ignoreOverridden"

@KotlinCoreEnvironmentTest
class MemberNameEqualsClassNameSpec(val env: KotlinCoreEnvironment) {
    val subject = MemberNameEqualsClassName(Config.empty)

    val noIgnoreOverridden =
        TestConfig(
            mapOf(
                IGNORE_OVERRIDDEN to "false"
            )
        )

    @Nested
    inner class `some classes with methods which don't have the same name` {

        @Test
        fun `does not report a nested function with the same name as the class`() {
            val code = """
                class MethodNameNotEqualsClassName {
                    fun nestedFunction() {
                        fun MethodNameNotEqualsClassName() {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report a function with same name in nested class`() {
            val code = """
                class MethodNameNotEqualsClassName {
                    class NestedNameEqualsTopClassName {
                        fun MethodNameNotEqualsClassName() {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report a function with the same name as a companion object`() {
            val code = """
                class StaticMethodNameEqualsObjectName {
                    companion object A {
                        fun A() {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `some classes with members which have the same name` {

        @Test
        fun `reports a method which is named after the class`() {
            val code = """
                class MethodNameEqualsClassName {
                    fun methodNameEqualsClassName() {}
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a method which is named after the object`() {
            val code = """
                object MethodNameEqualsObjectName {
                    fun MethodNameEqualsObjectName() {}
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a property which is named after the class`() {
            val code = """
                class PropertyNameEqualsClassName {
                    val propertyNameEqualsClassName = 0
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a property which is named after the object`() {
            val code = """
                object PropertyNameEqualsObjectName {
                    val propertyNameEqualsObjectName = 0
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a companion object function which is named after the class`() {
            val code = """
                class StaticMethodNameEqualsClassName {
                    companion object {
                        fun StaticMethodNameEqualsClassName() {}
                    }
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a method which is named after the class even when it's inside another one`() {
            val code = """
                class MethodNameContainer {
                    class MethodNameEqualsNestedClassName {
                        fun MethodNameEqualsNestedClassName() {}
                    }
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `doesn't report overridden methods which are named after the class`() {
            val code = """
                class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {
                    override fun AbstractMethodNameEqualsClassName() {}
                }
                abstract class BaseClassForMethodNameEqualsClassName {
                    abstract fun AbstractMethodNameEqualsClassName()
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).isEmpty()
        }

        @Test
        fun `doesn't report an methods which are named after the interface`() {
            val code = """
                interface MethodNameEqualsInterfaceName {
                    fun MethodNameEqualsInterfaceName() {}
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports overridden methods which are named after the class if they are not ignored`() {
            val code = """
                class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {
                    override fun AbstractMethodNameEqualsClassName() {}
                }
                abstract class BaseClassForMethodNameEqualsClassName {
                    abstract fun AbstractMethodNameEqualsClassName()
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName(noIgnoreOverridden).compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `doesn't report overridden properties which are named after the class`() {
            val code = """
                class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {
                    override val AbstractMethodNameEqualsClassName = ""
                }
                abstract class BaseClassForMethodNameEqualsClassName {
                    abstract val AbstractMethodNameEqualsClassName: String
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports overridden properties which are named after the class if they are not ignored`() {
            val code = """
                class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {
                    override val AbstractMethodNameEqualsClassName = ""
                }
                abstract class BaseClassForMethodNameEqualsClassName {
                    abstract val AbstractMethodNameEqualsClassName: String
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName(noIgnoreOverridden).compileAndLint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `some companion object functions named after the class (factory functions)` {

        @Test
        fun `reports a function which has no return type`() {
            val code = """
                class WrongFactoryClass1 {

                    companion object {
                        fun wrongFactoryClass1() {}
                    }
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a function which has the wrong return type`() {
            val code = """
                class WrongFactoryClass2 {

                    companion object {
                        fun wrongFactoryClass2(): Int {
                            return 0
                        }
                    }
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a body-less function which has the wrong return type`() {
            val code = """
                class WrongFactoryClass3 {
                
                    companion object {
                        fun wrongFactoryClass3() = 0
                    }
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `doesn't report a factory function`() {
            val code = """
                open class A {
                    companion object {
                        fun a(condition: Boolean): A {
                            return if (condition) B() else C()
                        }
                    }
                }
                
                class B: A()
                
                class C: A()
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `doesn't report a generic factory function`() {
            val code = """
                data class GenericClass<T>(val wrapped: T) {
                    companion object {
                        fun <T> genericClass(wrapped: T): GenericClass<T> {
                            return GenericClass(wrapped)
                        }
                        fun genericClass(): GenericClass<String> {
                            return GenericClass("wrapped")
                        }
                    }
                }
            """.trimIndent()
            assertThat(MemberNameEqualsClassName().compileAndLintWithContext(env, code)).isEmpty()
        }

        @Nested
        @DisplayName("doesn't report a body-less factory function")
        inner class IgnoreBodylessFactoryFunction {
            val code = """
                open class A {
                  companion object {
                    fun a(condition: Boolean) = if (condition) B() else C()
                  }
                }
                
                class B: A()

                class C: A()
            """.trimIndent()

            @Test
            fun `with type solving`() {
                assertThat(MemberNameEqualsClassName().compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `without type solving`() {
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).isEmpty()
            }
        }
    }
}
