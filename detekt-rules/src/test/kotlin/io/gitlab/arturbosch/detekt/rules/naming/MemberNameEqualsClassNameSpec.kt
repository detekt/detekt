package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MemberNameEqualsClassNameSpec : Spek({
    val subject by memoized { MemberNameEqualsClassName(Config.empty) }

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

            it("reports companion function which are named after the class and they are not a factory") {
                val code = """
                    class WrongFactoryClass {
                        companion object {
                            fun wrongFactoryClass(): Int {
                                return 0
                            }
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
        }
    }
})
