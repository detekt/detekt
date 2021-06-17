package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class VarCouldBeValSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { VarCouldBeVal() }

    describe("local declarations in functions") {

        it("does not report variables that are re-assigned") {
            val code = """
            fun test() {
                var a = 1
                a = 2
            }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report variables that are re-assigned with assignment operator") {
            val code = """
            fun test() {
                var a = 1
                a += 2
            }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report variables that are re-assigned with postfix operators") {
            val code = """
            fun test() {
                var a = 1
                a++
            }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report variables that are re-assigned with infix operators") {
            val code = """
            fun test() {
                var a = 1
                --a
            }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report variables that are re-assigned inside scope functions") {
            val code = """
            fun test() {
                var a = 1
                a.also {
                    a = 2
                }
            }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports variables that are not re-assigned, but used in expressions") {
            val code = """
            fun test() {
                var a = 1
                val b = a + 2
            }
            """
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].entity.signature).isEqualTo("Test.kt\$var a = 1")
        }

        it("reports variables that are not re-assigned, but used in function calls") {
            val code = """
            fun test() {
                var a = 1
                println(a)
            }
            """
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].entity.signature).isEqualTo("Test.kt\$var a = 1")
        }

        it("reports variables that are not re-assigned, but shadowed by one that is") {
            val code = """
            fun test() {
                var shadowed = 1
                fun nestedFunction() {
                    var shadowed = 2
                    shadowed = 3
                }
            }
            """
            val lint = subject.compileAndLintWithContext(env, code)

            assertThat(lint).hasSize(1)
            with(lint[0].entity) {
                assertThat(ktElement?.text).isEqualTo("var shadowed = 1")
            }
        }
    }

    describe("this-prefixed properties - #1257") {

        it("finds unused field and local") {
            val code = """
                fun createObject() = object {
                    private var myVar: String? = null
                    fun assign(value: String?) {
                        var myVar = value
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        it("should not report this-prefixed property") {
            val code = """
                fun createObject() = object {
                    private var myVar: String? = null
                    fun assign(value: String?) {
                        this.myVar = value
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("should report unused local variable") {
            val code = """
                fun createObject() = object {
                    private var myVar: String? = null
                    fun assign(value: String?) {
                        var myVar = value
                        this.myVar = value
                    }
                }
            """
            with(subject.compileAndLintWithContext(env, code)[0]) {
                assertThat(entity.ktElement?.text).isEqualTo("var myVar = value")
            }
        }
    }

    describe("properties defined in anonymous object - #3805") {
        it("should report unassigned properties") {
            val code = """
                fun test() {
                    val wrapper = object {
                        var test: Boolean = true
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should not report assigned properties") {
            val code = """
                fun test() {
                    val wrapper = object {
                        var test: Boolean = true
                    }
                    wrapper.test = false
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
