package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryParenthesesSpec : Spek({
    val subject by memoized { UnnecessaryParentheses(Config.empty) }

    describe("UnnecessaryParentheses rule") {

        it("with unnecessary parentheses on val assignment") {
            val code = "val local = (5)"
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("with unnecessary parentheses on val assignment operation") {
            val code = "val local = (5 + 3)"
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("with unnecessary parentheses on function call") {
            val code = "val local = 3.plus((5))"
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("unnecessary parentheses in other parentheses") {
            val code = """
                fun x(a: String, b: String) {
                    if ((a equals b)) {
                        println("Test")
                    }
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report unnecessary parentheses around lambdas") {
            val code = """
                fun function (a: (input: String) -> Unit) {
                    a.invoke("TEST")
                }

                fun test() {
                    function({ input -> println(input) })
                }
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("doesn't report function calls containing lambdas and other parameters") {
            val code = """
                fun function (integer: Int, a: (input: String) -> Unit) {
                    a.invoke("TEST")
                }

                fun test() {
                    function(1, { input -> println(input) })
                }
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report unnecessary parentheses when assigning a lambda to a val") {
            val code = """
                fun f() {
                    instance.copy(value = { false })
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report well behaved parentheses") {
            val code = """
                fun x(a: String, b: String) {
                    if (a equals b) {
                        println("Test")
                    }
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report well behaved parentheses in super constructors") {
            val code = """
                class TestSpek : SubjectSpek({
                    describe("a simple test") {
                        it("should do something") {
                        }
                    }
                })
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report well behaved parentheses in constructors") {
            val code = """
                class TestSpek({
                    describe("a simple test") {
                        it("should do something") {
                        }
                    }
                })
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("should not report lambdas within super constructor calls") {
            val code = """
                class Clazz(
                    private val func: (X, Y) -> Z
                ) {
                    constructor() : this({ first, second -> true })
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("should not report call to function with two lambda parameters with one as block body") {
            val code = """
                class Clazz {
                    fun test(first: (Int) -> Unit, second: (Int) -> Unit) {
                        first(1)
                        second(2)
                    }

                    fun call() {
                        test({ println(it) }) { println(it) }
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("should not report call to function with two lambda parameters") {
            val code = """
                class Clazz {
                    fun test(first: (Int) -> Unit, second: (Int) -> Unit) {
                        first(1)
                        second(2)
                    }

                    fun call() {
                        test({ println(it) }, { println(it) })
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("should not report call to function with multiple lambdas as parameters but also other parameters") {
            val code = """
                class Clazz {
                    fun test(text: String, first: () -> Unit, second: () -> Unit) {
                        first()
                        second()
                    }

                    fun call() {
                        test("hello", { println(it) }) { println(it) }
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
