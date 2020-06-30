package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MandatoryBracesIfStatementsSpec : Spek({
    val subject by memoized { MandatoryBracesIfStatements(Config.empty) }

    describe("if statements which should have braces") {

        it("reports a simple if") {
            val findings = subject.compileAndLint("""
            fun f() {
                if (true)
                    println()
            }
            """)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(32 to 41)
        }

        it("reports a simple if with a single statement in multiple lines") {
            val findings = subject.compileAndLint("""
                fun f() {
                	if (true) 50
                        .toString()
                }
            """)

            assertThat(findings).hasSize(1)
        }

        it("reports if-else with a single statement in multiple lines") {
            val findings = subject.compileAndLint("""
                fun f() {
                	if (true) 50
                        .toString() else 50
                        .toString()
                }
            """)

            assertThat(findings).hasSize(2)
        }

        it("reports if-else") {
            val findings = subject.compileAndLint("""
            fun f() {
                if (true)
                    println()
                else
                    println()
            }
            """)

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(32 to 41, 59 to 68)
        }

        it("reports if-else with else-if") {
            val findings = subject.compileAndLint("""
            fun f() {
                if (true)
                    println()
                else if (false)
                    println()
                else
                    println()
            }
            """)

            assertThat(findings).hasSize(3)
            assertThat(findings).hasTextLocations(32 to 41, 70 to 79, 97 to 106)
        }

        it("reports if with braces but else without") {
            val findings = subject.compileAndLint("""
            fun f() {
                if (true) {
                    println()
                } else
                    println()
            }
            """)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(63 to 72)
        }

        it("reports else with braces but if without") {
            val findings = subject.compileAndLint("""
            fun f() {
                if (true)
                    println()
                else {
                    println()
                }
            }
            """)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(32 to 41)
        }

        it("reports else in new line") {
            val findings = subject.compileAndLint("""
            fun f() {
                if (true) println()
                else println()
            }
            """)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(24 to 33)
        }

        it("reports only else body on new line") {
            val findings = subject.compileAndLint("""
            fun f() {
                if (true) println() else
                    println()
            }
            """)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(47 to 56)
        }
    }

    describe("if statements with braces") {

        it("does not report if statements with braces") {
            val code = """
                fun f() {
                	if (true) {
                		println()
                	}
                	if (true)
                	{
                		println()
                	}
                	if (true) { println() }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    describe("single-line if statements which don't need braces") {

        it("does not report single-line if statements") {
            val code = """
                fun f() {
                	if (true) println()
                	if (true) println() else println()
                	if (true) println() else if (false) println() else println()
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
