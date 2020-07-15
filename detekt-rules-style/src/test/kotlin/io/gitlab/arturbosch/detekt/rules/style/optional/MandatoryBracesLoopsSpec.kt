package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MandatoryBracesLoopsSpec : Spek({
    val subject by memoized { MandatoryBracesLoops() }

    describe("MandatoryBracesLoops rule for `for` loops") {

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

        it("does not report full loop on single line with multiple statements") {
            val code = """
            fun test() {
                for (i in 0..10) println(i); print(' ')
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
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
            assertThat(findings[0].entity.ktElement?.text).isEqualTo("println(i)")
        }

        it("does not report on suppression") {
            val code = """
            fun test() {
                @Suppress("MandatoryBracesLoops")
                for (i in 0..10)
                    println(i)
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nested loops with braces") {
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

        it("does not report nested loops on single line") {
            val code = """
            fun test() {
                for (i in 0..10) for (j in 0..10) println()
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports in nested loop outer") {
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
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
            assertThat(findings[0].location.source).isEqualTo(SourceLocation(line = 3, column = 9))
        }

        it("reports in nested loop inner") {
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
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
        }

        it("reports both violations in nested loop") {
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
                assertThat(finding.id).isEqualTo("MandatoryBracesLoops")
            }

            io.gitlab.arturbosch.detekt.test.assertThat(findings).hasTextLocations(42 to 80, 71 to 80)
        }

        it("reports with multi-line if statement") {
            val code = """
            fun test() {
                // because if statements are expressions, this code properly prints "Odd" and "Even" 
                for (i in 0..10) 
                    if (i % 2 == 0) {
                        println("Even")
                    } else {
                        println("Odd")
                    }
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
            assertThat(findings[0].location.source).isEqualTo(SourceLocation(line = 4, column = 9))
        }

        it("reports inside of if statement without braces") {
            val code = """
            fun test() {
                // this if statement would also be reported, but we're only checking the loop
                val i = 2
                if (i % 2 == 0)
                    for (j in 0..10) 
                        println(i)
                else {
                    println("Odd")
                }
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
            assertThat(findings[0].location.source).isEqualTo(SourceLocation(line = 6, column = 13))
        }
    }

    describe("MandatoryBracesLoops rule for `while` loops") {

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
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
            assertThat(findings[0].entity.ktElement?.text).isEqualTo("println()")
        }

        it("does not report on suppression") {
            val code = """
            fun test() {
                @Suppress("MandatoryBracesLoops")
                while(true)
                    println()
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports in nested loop inner") {
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
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
        }
    }

    describe("MandatoryBracesLoops rule for `do while` loops") {

        it("does not report with braces") {
            val code = """
            fun test() {
                do {
                    println()
                } while(true)
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report full loop on single line") {
            val code = """
            fun test() {
                do println() while(true)
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports multi-line without braces") {
            val code = """
            fun test() {
                do
                    println()
                while (true)
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
            assertThat(findings[0].entity.ktElement?.text).isEqualTo("println()")
        }

        it("does not report on suppression") {
            val code = """
            fun test() {
                @Suppress("MandatoryBracesLoops")
                do
                    println()
                while(true)
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nested loops with braces") {
            val code = """		
            fun test() {		
                do {		
                    while (true) {		
                        println()		
                    }		
                } while (true)	
            }		
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report nested loops on single line") {
            val code = """		
            fun test() {		
                var i = 0
                do do i += 1 while(i < 5) while (i < 5)	
            }		
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports in nested loop outer") {
            val code = """		
            fun test() {		
                do 		
                    do {		
                        println()		
                    } while (true)
                while (true)
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
            assertThat(findings[0].location.source).isEqualTo(SourceLocation(line = 3, column = 9))
        }

        it("reports in nested loop inner") {
            val code = """
            fun test() {
                do {
                    do
                        println()
                    while(true)
                } while (true)
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesLoops")
        }
    }
})
