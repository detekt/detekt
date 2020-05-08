package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MandatoryBracesForLoopsSpec : Spek({
    val subject by memoized { MandatoryBracesForLoops() }

    describe("MandatoryBracesForLoops rule for `for` loops") {

        it("does not report with braces") {
            val code = """
            fun test() {
                for (i in 0..10) {
                    println(i)
                }
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report full loop on single line") {
            val code = """
            fun test() {
                for (i in 0..10) println(i)
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports multi-line without braces") {
            val code = """
            fun test() {
                for (i in 0..10)
                    println(i)
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
            assertThat(findings[0].entity.ktElement?.text).isEqualTo("println(i)")
        }

        it("does not report on suppression") {
            val code = """
            fun test() {
                @Suppress("MandatoryBracesForLoops")
                for (i in 0..10)
                    println(i)
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it ("does not report nested loops with braces") {
            val code = """
            fun test() {
                for (i in 0..10) {
                    for (j in 0..10) {
                        println()
                    }
                }
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it ("does not report nested loops on single line") {
            val code = """
            fun test() {
                for (i in 0..10) for (j in 0..10) println()
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it ("reports in nested loop outer") {
            val code = """
            fun test() {
                for (i in 0..10) 
                    for (j in 0..10) {
                        println()
                    }
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
            assertThat(findings[0].location.source).isEqualTo(SourceLocation(line = 3, column = 9))
        }

        it ("reports in nested loop inner") {
            val code = """
            fun test() {
                for (i in 0..10) {
                    for (j in 0..10)
                        println()
                }
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
        }

        it ("reports both violations in nested loop") {
            val code = """
            fun test() {
                for (i in 0..10)
                    for (j in 0..10)
                        println()
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(2)
            for (finding in findings) {
                assertThat(finding.id).isEqualTo("MandatoryBracesForLoops")
            }

            io.gitlab.arturbosch.detekt.test.assertThat(findings).hasTextLocations(42 to 80, 71 to 80)
        }

        it ("reports with multi-line if statement") {
            val code = """
            fun test() {
                // because if statements are expressions, this code properly prints "Odd" and "Even" 
                for (i in 0..10) 
                    if (i % 2 == 0) {
                        println("Odd")
                    } else {
                        println("Even")
                    }
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
            assertThat(findings[0].location.source).isEqualTo(SourceLocation(line = 4, column = 9))
        }
    }

    describe("MandatoryBracesForLoops rule for `while` loops") {

        it("does not report with braces") {
            val code = """
            fun test() {
                while(true) {
                    println()
                }
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report full loop on single line") {
            val code = """
            fun test() {
                while(true) println()
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports multi-line without braces") {
            val code = """
            fun test() {
                while (true)
                    println()
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
            assertThat(findings[0].entity.ktElement?.text).isEqualTo("println()")
        }

        it("does not report on suppression") {
            val code = """
            fun test() {
                @Suppress("MandatoryBracesForLoops")
                while(true)
                    println()
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it ("does not report nested loops with braces") {
            val code = """
            fun test() {
                while (true) {
                    while (true) {
                        println()
                    }
                }
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it ("does not report nested loops on single line") {
            val code = """
            fun test() {
                while (true) while (true) println()
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it ("reports in nested loop outer") {
            val code = """
            fun test() {
                while (true) 
                    while (true) {
                        println()
                    }
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
            assertThat(findings[0].location.source).isEqualTo(SourceLocation(line = 3, column = 9))
        }

        it ("reports in nested loop inner") {
            val code = """
            fun test() {
                while (true) {
                    while (true)
                        println()
                }
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
        }

        it ("reports both violations in nested loop") {
            val code = """
            fun test() {
                while (true)
                    while (true)
                        println()
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(2)
            for (finding in findings) {
                assertThat(finding.id).isEqualTo("MandatoryBracesForLoops")
            }

            io.gitlab.arturbosch.detekt.test.assertThat(findings).hasTextLocations(38 to 72, 63 to 72)
        }

        it ("reports with multi-line if statement") {
            val code = """
            fun test() {
                // because if statements are expressions, this code properly prints "Odd" and "Even" 
                while (true) 
                    if (i % 2 == 0) {
                        println("Odd")
                    } else {
                        println("Even")
                    }
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
            assertThat(findings[0].location.source).isEqualTo(SourceLocation(line = 4, column = 9))
        }
    }
})
