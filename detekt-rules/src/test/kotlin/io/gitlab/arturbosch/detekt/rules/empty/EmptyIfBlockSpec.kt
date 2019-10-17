package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EmptyIfBlockSpec : Spek({

    val subject by memoized { EmptyIfBlock(Config.empty) }

    describe("EmptyIfBlock rule") {

        it("reports empty if with trailing semicolon") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0);
                    i++
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports empty if with trailing semicolon on new line") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0)
                    ;
                    i++
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports empty if with trailing semicolon and braces") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0); {
                    }
                    i++
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report nonempty if with braces") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        i++
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nonempty if without braces") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) i++
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nonempty if without braces but semicolon") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) i++;
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report empty if but nonempty else") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) ;
                    else i++
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report empty if and else-if but nonempty else") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) ;
                    else if (i == 1) ;
                    else i++
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
