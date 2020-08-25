package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class VarCouldBeValSpec : Spek({

    val subject by memoized { VarCouldBeVal() }

    describe("local declarations in functions") {

        it("does not report variables that are re-assigned") {
            val code = """
            fun test() {
                var a = 1
                a = 2
            }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report variables that are re-assigned with assignment operator") {
            val code = """
            fun test() {
                var a = 1
                a += 2
            }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report variables that are re-assigned with postfix operators") {
            val code = """
            fun test() {
                var a = 1
                a++
            }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report variables that are re-assigned with infix operators") {
            val code = """
            fun test() {
                var a = 1
                --a
            }
            """
            assertThat(subject.lint(code)).isEmpty()
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
            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports variables that are not re-assigned, but used in expressions") {
            val code = """
            fun test() {
                var a = 1
                val b = a + 2
            }
            """
            val findings = subject.lint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].entity.signature).isEqualTo("Test.kt\$var a = 1")
        }

        it("reports variables that are not re-assigned, but used in function calls") {
            val code = """
            fun test() {
                var a = 1
                something(a)
            }
            """
            val findings = subject.lint(code)

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
            val lint = subject.lint(code)

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
            assertThat(subject.lint(code)).hasSize(2)
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
            assertThat(subject.lint(code)).isEmpty()
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
            with(subject.lint(code)[0]) {
                // we accept wrong entity reporting here due to no type resolution
                // false reporting with shadowed vars vs false positives
                assertThat(entity.ktElement?.text).isEqualTo("private var myVar: String? = null")
            }
        }
    }
})
