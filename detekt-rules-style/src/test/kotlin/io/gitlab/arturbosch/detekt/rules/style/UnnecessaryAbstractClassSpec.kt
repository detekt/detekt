package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryAbstractClassSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized {
        UnnecessaryAbstractClass(TestConfig(mapOf(
            UnnecessaryAbstractClass.EXCLUDE_ANNOTATED_CLASSES to listOf("Deprecated")
        )))
    }

    describe("UnnecessaryAbstractClass rule") {

        context("abstract classes with no concrete members") {

            it("reports an abstract class with no concrete member") {
                val code = """
                    abstract class A {
                        abstract val i: Int
                        abstract fun f()
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(
                    findings,
                    "An abstract class without a concrete member can be refactored to an interface."
                )
            }

            it("does not report an abstract class with concrete members derived from a base class") {
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
        }

        context("abstract classes with no abstract members") {

            val message = "An abstract class without an abstract member can be refactored to a concrete class."

            it("reports no abstract members in abstract class") {
                val code = """
                    abstract class A {
                        val i: Int = 0
                        fun f() {}
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
            }

            it("reports no abstract members in nested abstract class inside a concrete class") {
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

            it("reports no abstract members in nested abstract class inside an interface") {
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

            it("reports no abstract members in an abstract class with just a constructor") {
                val code = "abstract class A(val i: Int)"
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
            }

            it("reports no abstract members in an abstract class with a body and a constructor") {
                val code = "abstract class A(val i: Int) {}"
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingMessage(findings, message)
            }

            it("reports an abstract class with no abstract member derived from a class with abstract members") {
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

        context("abstract classes with members") {

            it("does not report an abstract class with members and an abstract class derived from it") {
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

            it("does not report an abstract class with a constructor and an abstract class derived from it") {
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

            it("does not report an abstract class with a function derived from an interface") {
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

            it("does not report empty abstract classes") {
                val code = """
                    abstract class A
                    abstract class B()
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report abstract classes with module annotation") {
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
        }
    }
})

private fun assertFindingMessage(findings: List<Finding>, message: String) {
    assertThat(findings).hasSize(1)
    assertThat(findings.first().message).isEqualTo(message)
}
