package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MemberNameEqualsClassNameSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { MemberNameEqualsClassName(Config.empty) }

    describe("MemberNameEqualsClassName rule") {

        val noIgnoreOverridden by memoized {
            TestConfig(
                mapOf(
                    MemberNameEqualsClassName.IGNORE_OVERRIDDEN_FUNCTION to "false"
                )
            )
        }

        context("some classes with methods which don't have the same name") {

            it("does not report a nested function with the same name as the class") {
                val code = """
                    class MethodNameNotEqualsClassName {
                        fun nestedFunction() {
                            fun MethodNameNotEqualsClassName() {}
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report a function with same name in nested class") {
                val code = """
                    class MethodNameNotEqualsClassName {
                        class NestedNameEqualsTopClassName {
                            fun MethodNameNotEqualsClassName() {}
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report a function with the same name as a companion object") {
                val code = """
                    class StaticMethodNameEqualsObjectName {
                        companion object A {
                            fun A() {}
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("some classes with members which have the same name") {

            it("reports a method which is named after the class") {
                val code = """
                    class MethodNameEqualsClassName {
                        fun methodNameEqualsClassName() {}
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports a method which is named after the object") {
                val code = """
                    object MethodNameEqualsObjectName {
                        fun MethodNameEqualsObjectName() {}
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports a property which is named after the class") {
                val code = """
                    class PropertyNameEqualsClassName {
                        val propertyNameEqualsClassName = 0
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports a property which is named after the object") {
                val code = """
                    object PropertyNameEqualsObjectName {
                        val propertyNameEqualsObjectName = 0
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports a companion object function which is named after the class") {
                val code = """
                    class StaticMethodNameEqualsClassName {
                        companion object {
                            fun StaticMethodNameEqualsClassName() {}
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports a method which is named after the class even when it's inside another one") {
                val code = """
                    class MethodNameContainer {
                        class MethodNameEqualsNestedClassName {
                            fun MethodNameEqualsNestedClassName() {}
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("doesn't report overridden methods which are named after the class") {
                val code = """
                    class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {
                        override fun AbstractMethodNameEqualsClassName() {}
                    }
                    abstract class BaseClassForMethodNameEqualsClassName {
                        abstract fun AbstractMethodNameEqualsClassName()
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).isEmpty()
            }

            it("doesn't report an methods which are named after the interface") {
                val code = """
                    interface MethodNameEqualsInterfaceName {
                        fun MethodNameEqualsInterfaceName() {}
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).isEmpty()
            }

            it("reports overridden methods which are named after the class if they are not ignored") {
                val code = """
                    class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {
                        override fun AbstractMethodNameEqualsClassName() {}
                    }
                    abstract class BaseClassForMethodNameEqualsClassName {
                        abstract fun AbstractMethodNameEqualsClassName()
                    }
                """
                assertThat(MemberNameEqualsClassName(noIgnoreOverridden).compileAndLint(code)).hasSize(1)
            }

            it("doesn't report overridden properties which are named after the class") {
                val code = """
                    class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {
                        override val AbstractMethodNameEqualsClassName = ""
                    }
                    abstract class BaseClassForMethodNameEqualsClassName {
                        abstract val AbstractMethodNameEqualsClassName: String
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).isEmpty()
            }

            it("reports overridden properties which are named after the class if they are not ignored") {
                val code = """
                    class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {
                        override val AbstractMethodNameEqualsClassName = ""
                    }
                    abstract class BaseClassForMethodNameEqualsClassName {
                        abstract val AbstractMethodNameEqualsClassName: String
                    }
                """
                assertThat(MemberNameEqualsClassName(noIgnoreOverridden).compileAndLint(code)).hasSize(1)
            }
        }

        context("some companion object functions named after the class (factory functions)") {

            it("reports a function which has no return type") {
                val code = """
                    class WrongFactoryClass1 {

                        companion object {
                            fun wrongFactoryClass1() {}
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports a function which has the wrong return type") {
                val code = """
                    class WrongFactoryClass2 {

                        companion object {
                            fun wrongFactoryClass2(): Int {
                                return 0
                            }
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports a body-less function which has the wrong return type") {
                val code = """
                    class WrongFactoryClass3 {
                    
                        companion object {
                            fun wrongFactoryClass3() = 0
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("doesn't report a factory function") {
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
                """
                assertThat(MemberNameEqualsClassName().compileAndLintWithContext(env, code)).isEmpty()
            }

            it("doesn't report a body-less factory function") {
                val code = """
                    open class A {
                      companion object {
                        fun a(condition: Boolean) = if (condition) B() else C()
                      }
                    }
                    
                    class B: A()

                    class C: A()
                """
                assertThat(MemberNameEqualsClassName().compileAndLintWithContext(env, code)).isEmpty()
            }
        }
    }
})
