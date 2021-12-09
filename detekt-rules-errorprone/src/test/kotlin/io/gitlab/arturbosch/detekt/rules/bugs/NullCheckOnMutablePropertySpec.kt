package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NullCheckOnMutablePropertySpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { NullCheckOnMutableProperty(Config.empty) }

    describe("NullCheckOnMutableProperty Rule") {
        it("should report a null-check on a mutable constructor property") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        if (a != null) {
                            println(2 + a!!)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should report on a mutable property that is not subject to a double-bang") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        if (a != null) {
                            println(a)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should report on a mutable property even if it is checked multiple times") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        if (a != null) {
                            if (a == null) {
                                return
                            }
                            println(2 + a!!)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should not report when the checked property is not used afterwards") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        if (a != null) {
                            println("'a' is not null")
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("should not report a null-check on a shadowed property") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        val a = a
                        if (a != null) {
                            println(2 + a)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("should not report a null-check on a non-mutable constructor property") {
            val code = """
                class A(private val a: Int?) {
                    fun foo() {
                        if (a != null) {
                            println(2 + a)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("should report a null-check on a mutable class property") {
            val code = """
                class A {
                    private var a: Int? = 5
                    fun foo() {
                        if (a != null) {
                            println(2 + a!!)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should not report a null-check on a non-mutable class property") {
            val code = """
                class A {
                    private val a: Int? = 5
                    fun foo() {
                        if (a != null) {
                            println(2 + a)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("should report a null-check conducted within an inner class") {
            val code = """
                class A(private var a: Int?) {
                    inner class B {
                        fun foo() {
                            if (a != null) {
                                println(2 + a!!)
                            } 
                        }
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should report an inner-class mutable property") {
            val code = """
                class A(private val a: Int?) {
                    inner class B(private var a: Int) {
                        fun foo() {
                            if (a != null) {
                                println(2 + a!!)
                            } 
                        }
                    }

                    fun foo() {
                        if (a != null) {
                            println(2 + a)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should report a null-check on a mutable file property") {
            val code = """
                private var a: Int? = 5
                
                class A {
                    fun foo() {
                        if (a != null) {
                            println(2 + a!!)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should not report a null-check on a non-mutable file property") {
            val code = """
                private val a: Int? = 5
                
                class A {
                    fun foo() {
                        if (a != null) {
                            println(2 + a)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("should not report a null-check when there is no binding context") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        if (a != null) {
                            println(2 + a!!)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should report a null-check when null is the first element in the if-statement") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        if (null != a) {
                            println(2 + a!!)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should not report when the if-expression has no explicit null value") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        val otherA = null
                        if (a != otherA) {
                            println(2 + a!!)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("should not report a null-check on a function") {
            val code = """
                class A {
                    private fun otherFoo(): Int? {
                        return null
                    }
                    fun foo() {
                        if (otherFoo() != null) {
                            println(2 + otherFoo()!!)
                        } 
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
