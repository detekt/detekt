package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CollapsibleIfStatementsSpec : Spek({
    val subject by memoized { CollapsibleIfStatements(Config.empty) }

    describe("CollapsibleIfStatements rule") {

        it("reports if statements which can be merged") {
            val code = """
                fun f() {
                    if (true) {
                        if (1 == 1) {}
                        // a comment
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports nested if statements which can be merged") {
            val code = """
                fun f() {
                    if (true) {
                        if (1 == 1) {
                            if (2 == 2) {}
                        }
                        println()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report else-if") {
            val code = """
                fun f() {
                    if (true) {}
                    else if (1 == 1) {
                        if (true) {}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report if-else") {
            val code = """
                fun f() {
                    if (true) {
                        if (1 == 1) {}
                    } else {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report if-elseif-else") {
            val code = """
                fun f() {
                    if (true) {
                        if (1 == 1) {}
                    } else if (false) {}
                    else {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report if with statements in the if body") {
            val code = """
                fun f() {
                    if (true) {
                        if (1 == 1) ;
                        println()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nested if-else") {
            val code = """
                fun f() {
                    if (true) {
                        if (1 == 1) {
                        } else {}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nested if-elseif") {
            val code = """
                fun f() {
                    if (true) {
                        if (1 == 1) {
                        } else if (2 == 2) {}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
