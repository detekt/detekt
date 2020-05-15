package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnreachableCodeSpec : Spek({
    val subject by memoized { UnreachableCode(Config.empty) }

    describe("UnreachableCode rule") {

        it("reports unreachable code after return") {
            val code = """
                fun f(i: Int) {
                    if (i == 0) {
                        return
                        println()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports unreachable code after return in lambda") {
            val code = """
                fun f(s: String): Boolean {
                    s.let {
                        return it.length < 3
                        println()
                    }
                    return false
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports unreachable code after return with label") {
            val code = """
                fun f(ints: List<Int>): List<Int> {
                    return ints.map f@{
                        if (it == 0) {
                            return@f 0
                            println()
                        }
                        return@f 1
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports unreachable code after throwing an exception") {
            val code = """
                fun f(i: Int) {
                    if (i == 0) {
                        throw IllegalArgumentException()
                        println()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports unreachable code after break and continue") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        break
                        println()
                    }
                    for (i in 1..2) {
                        continue
                        println()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("does not report reachable code after conditional return with label") {
            val code = """
                fun f(ints: List<Int>) {
                    ints.forEach {
                        if (it == 0) return@forEach
                        println()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report reachable code after if") {
            val code = """
                fun f(i: Int) {
                    if (i == 0) {
                        println()
                    }
                    throw IllegalArgumentException()
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report reachable code in if body") {
            val code = """
                fun f(i: Int) {
                    if (i == 0) {
                        println(i)
                        throw IllegalArgumentException()
                    }
                    println()
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
