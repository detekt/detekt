package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UselessPostfixExpressionSpec : Spek({
    val subject by memoized { UselessPostfixExpression() }

    describe("check several types of postfix increments") {

        it("overrides the incremented integer") {
            val code = """
                fun f() {
                    var i = 0
                    i = i-- // invalid
                    i = 1 + i++ // invalid
                    i = i++ + 1 // invalid
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(3)
        }

        it("does not override the incremented integer") {
            val code = """
                fun f() {
                    var i = 0
                    var j = 0
                    j = i++
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("returns no incremented value") {
            val code = """
                fun f(): Int {
                    var i = 0
                    var j = 0
                    if (i == 0) return 1 + j++
                    return i++
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("should not report field increments") {
            val code = """
                class Test {
                    private var runningId: Long = 0

                    fun increment() {
                        runningId++
                    }

                    fun getId(): Long {
                        return runningId++
                    }
                }

                class Foo(var i: Int = 0) {
                    fun getIdAndIncrement(): Int {
                        return i++
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should detect properties shadowing fields that are incremented") {
            val code = """
                class Test {
                    private var runningId: Long = 0

                    fun getId(): Long {
                        var runningId: Long = 0
                        return runningId++
                    }
                }

                class Foo(var i: Int = 0) {
                    fun foo(): Int {
                        var i = 0
                        return i++
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }
    }

    describe("Only ++ and -- postfix operators should be considered") {

        it("should not report !! in a return statement") {
            val code = """
                val str: String? = ""

                fun f1(): String {
                    return str!!
                }

                fun f2(): Int {
                    return str!!.count()
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not report !! in a standalone expression") {
            val code = """
                fun f() {
                    val str: String? = ""
                    str!!
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
