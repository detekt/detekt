package io.gitlab.arturbosch.detekt.rules.style.optional

import dev.detekt.api.Config
import dev.detekt.api.SourceLocation
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MandatoryBracesLoopsSpec {
    val subject = MandatoryBracesLoops(Config.empty)

    @Nested
    inner class `MandatoryBracesLoops rule for 'for' loops` {

        @Test
        fun `does not report with braces`() {
            val code = """
                fun test() {
                    for (i in 0..10) {
                        println(i)
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report full loop on single line`() {
            val code = """
                fun test() {
                    for (i in 0..10) println(i)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report full loop on single line with multiple statements`() {
            val code = """
                fun test() {
                    for (i in 0..10) println(i); print(' ')
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports multi-line without braces`() {
            val code = """
                fun test() {
                    for (i in 0..10)
                        println(i)
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).singleElement()
                .hasTextLocation("println(i)")
        }

        @Test
        fun `does not report on suppression`() {
            val code = """
                fun test() {
                    @Suppress("MandatoryBracesLoops")
                    for (i in 0..10)
                        println(i)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report nested loops with braces`() {
            val code = """
                fun test() {
                    for (i in 0..10) {
                        for (j in 0..10) {
                            println()
                        }
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report nested loops on single line`() {
            val code = """
                fun test() {
                    for (i in 0..10) for (j in 0..10) println()
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports in nested loop outer`() {
            val code = """
                fun test() {
                    for (i in 0..10)
                        for (j in 0..10) {
                            println()
                        }
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).singleElement()
                .hasStartSourceLocation(SourceLocation(line = 3, column = 9))
        }

        @Test
        fun `reports in nested loop inner`() {
            val code = """
                fun test() {
                    for (i in 0..10) {
                        for (j in 0..10)
                            println()
                    }
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports both violations in nested loop`() {
            val code = """
                fun test() {
                    for (i in 0..10)
                        for (j in 0..10)
                            println()
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).hasSize(2)
            assertThat(findings).element(0)
                .hasTextLocation(42 to 80)
            assertThat(findings).element(1)
                .hasTextLocation(71 to 80)
        }

        @Test
        fun `reports with multi-line if statement`() {
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
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).singleElement()
                .hasStartSourceLocation(SourceLocation(line = 4, column = 9))
        }

        @Test
        fun `reports inside of if statement without braces`() {
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
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).singleElement()
                .hasStartSourceLocation(SourceLocation(line = 6, column = 13))
        }
    }

    @Nested
    inner class `MandatoryBracesLoops rule for 'while' loops` {

        @Test
        fun `does not report with braces`() {
            val code = """
                fun test() {
                    while(true) {
                        println()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report full loop on single line`() {
            val code = """
                fun test() {
                    while(true) println()
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports multi-line without braces`() {
            val code = """
                fun test() {
                    while (true)
                        println()
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).singleElement()
                .hasTextLocation("println()")
        }

        @Test
        fun `does not report on suppression`() {
            val code = """
                fun test() {
                    @Suppress("MandatoryBracesLoops")
                    while(true)
                        println()
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports in nested loop inner`() {
            val code = """
                fun test() {
                    while (true) {
                        while (true)
                            println()
                    }
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `MandatoryBracesLoops rule for 'do while' loops` {

        @Test
        fun `does not report with braces`() {
            val code = """
                fun test() {
                    do {
                        println()
                    } while(true)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report full loop on single line`() {
            val code = """
                fun test() {
                    do println() while(true)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports multi-line without braces`() {
            val code = """
                fun test() {
                    do
                        println()
                    while (true)
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).singleElement()
                .hasTextLocation("println()")
        }

        @Test
        fun `does not report on suppression`() {
            val code = """
                fun test() {
                    @Suppress("MandatoryBracesLoops")
                    do
                        println()
                    while(true)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report nested loops with braces`() {
            val code = """
                fun test() {
                    do {
                        while (true) {
                            println()
                        }
                    } while (true)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report nested loops on single line`() {
            val code = """
                fun test() {
                    var i = 0
                    do do i += 1 while(i < 5) while (i < 5)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports in nested loop outer`() {
            val code = """
                fun test() {
                    do
                        do {
                            println()
                        } while (true)
                    while (true)
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).singleElement()
                .hasStartSourceLocation(SourceLocation(line = 3, column = 9))
        }

        @Test
        fun `reports in nested loop inner`() {
            val code = """
                fun test() {
                    do {
                        do
                            println()
                        while(true)
                    } while (true)
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).hasSize(1)
        }
    }
}
