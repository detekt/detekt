package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EmptyElseBlockSpec : Spek({

    val subject by memoized { EmptyElseBlock(Config.empty) }

    describe("EmptyElseBlock rule") {
        it("reports empty else block") {
            val code = """
                fun f() {
                    val i = 0
                    if (i == 0) {
                        println(i)
                    } else {
                        
                    }
                }
            """
            Assertions.assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports empty else blocks with trailing semicolon") {
            val code = """
                fun f() {
                    val i = 0
                    if (i == 0) {
                        println(i)
                    } else ;
                }
            """
            Assertions.assertThat(subject.compileAndLint(code)).hasSize(1)
        }
        it("reports empty else with trailing semicolon on new line") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println(i)
                    } else
                    ;
                    i++
                }
            """
            Assertions.assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports empty else with trailing semicolon and braces") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println()
                    } else; {
                    }
                    i++
                }
            """
            Assertions.assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report nonempty else with braces") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println(i)
                    } else {
                        i++
                    }
                }
            """
            Assertions.assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nonempty else without braces") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println(i)
                    } else i++
                }
            """
            Assertions.assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nonempty else without braces but semicolon") {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println(i)
                    } else i++;
                }
            """
            Assertions.assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
