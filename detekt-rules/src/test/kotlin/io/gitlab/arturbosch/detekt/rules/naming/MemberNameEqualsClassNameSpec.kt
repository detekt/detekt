package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MemberNameEqualsClassNameSpec : Spek({
    val subject by memoized { MemberNameEqualsClassName(Config.empty) }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("MemberNameEqualsClassName rule") {

        val noIgnoreOverridden = TestConfig(
            mapOf(
                MemberNameEqualsClassName.IGNORE_OVERRIDDEN_FUNCTION to "false"
            )
        )

        context("some classes with methods which don't have the same name") {

            it("reports methods which are not named after the class") {
                val path = Case.MemberNameEqualsClassNameNegative.path()
                assertThat(subject.lint(path)).isEmpty()
            }
        }

        context("some classes with members which have the same name") {

            it("reports method which are named after the class") {
                val code = """
                    class MethodNameEqualsClassName {
                        fun methodNameEqualsClassName() {}
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports method which are named after the object") {
                val code = """
                    object MethodNameEqualsObjectName {
                        fun MethodNameEqualsObjectName() {}
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports property which are named after the class") {
                val code = """
                    class PropertyNameEqualsClassName {
                        val propertyNameEqualsClassName = 0
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports property which are named after the object") {
                val code = """
                    object PropertyNameEqualsObjectName {
                        val propertyNameEqualsObjectName = 0
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports companion function which are named after the class") {
                val code = """
                    class StaticMethodNameEqualsClassName {
                        companion object {
                            fun StaticMethodNameEqualsClassName() {}
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports method which are named after the class even when it's inside another one") {
                val code = """
                    class MethodNameContainer {
                        class MethodNameEqualsNestedClassName {
                            fun MethodNameEqualsNestedClassName() {}
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports companion function which are named after the class and they are not a factory 1") {
                val code = """
                    class WrongFactoryClass1 {

                        companion object {
                            fun wrongFactoryClass1() {} // reports 1 - no return type
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports companion function which are named after the class and they are not a factory 2") {
                val code = """
                    class WrongFactoryClass2 {

                        companion object {
                            fun wrongFactoryClass2(): Int { // reports 1 - wrong return type
                                return 0
                            }
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLint(code)).hasSize(1)
            }

            it("reports companion function which are named after the class and they are not a factory 3") {
                val code = """
                    class WrongFactoryClass3 {
                    
                        companion object {
                            fun wrongFactoryClass3() = 0 // reports 1 - wrong return type
                        }
                    }
                """
                assertThat(MemberNameEqualsClassName().compileAndLintWithContext(wrapper.env, code)).hasSize(1)
            }

            it("doesn't report companion function which are factory 1") {
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
                assertThat(MemberNameEqualsClassName().compileAndLintWithContext(wrapper.env, code)).isEmpty()
            }

            it("doesn't report companion function which are factory 2") {
                val code = """
                    open class A {
                      companion object {
                        fun a(condition: Boolean) = if (condition) B() else C()
                      }
                    }
                    
                    class B: A()

                    class C: A()
                """
                assertThat(MemberNameEqualsClassName().compileAndLintWithContext(wrapper.env, code)).isEmpty()
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
    }
})
