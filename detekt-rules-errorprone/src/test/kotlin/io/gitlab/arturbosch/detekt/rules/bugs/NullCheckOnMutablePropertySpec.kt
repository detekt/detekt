package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
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
    }
})
