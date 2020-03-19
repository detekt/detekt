package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MandatoryBracesIfStatementsSpec : Spek({
    val subject by memoized { MandatoryBracesIfStatements(Config.empty) }

    describe("reports multi-line if statements should have braces") {

        it("simple if") {
            val findings = subject.lint("""
            fun f() {
                if (true)
                    println()
            }
            """.trimIndent())

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(32 to 41)
        }

        it("if-else") {
            val findings = subject.lint("""
            fun f() {
                if (true)
                    println()
                else
                    println()
            }
            """.trimIndent())

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(32 to 41, 59 to 68)
        }

        it("if-else with else-if") {
            val findings = subject.lint("""
            fun f() {
                if (true)
                    println()
                else if (false)
                    println()
                else
                    println()
            }
            """.trimIndent())

            assertThat(findings).hasSize(3)
            assertThat(findings).hasTextLocations(32 to 41, 70 to 79, 97 to 106)
        }

        it("if with braces but else without") {
            val findings = subject.lint("""
            fun f() {
                if (true) {
                    println()
                } else
                    println()
            }
            """.trimIndent())

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(63 to 72)
        }

        it("else with braces but if without") {
            val findings = subject.lint("""
            fun f() {
                if (true)
                    println()
                else {
                    println()
                }
            }
            """.trimIndent())

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(32 to 41)
        }

        it("else in new line") {
            val findings = subject.lint("""
            fun f() {
                if (true) println()
                else println()
            }
            """.trimIndent())

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(24 to 33)
        }

        it("only else body in new line") {
            val findings = subject.lint("""
            fun f() {
                if (true) println() else
                    println()
            }
            """.trimIndent())

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(47 to 56)
        }
    }

    describe("MandatoryBracesIfStatements rule") {

        it("reports non multi-line if statements should have braces") {
            val path = Case.MandatoryBracesIfStatementsNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
