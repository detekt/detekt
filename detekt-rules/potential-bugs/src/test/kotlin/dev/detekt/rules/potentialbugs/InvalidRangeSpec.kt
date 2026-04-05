package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class InvalidRangeSpec {
    private val subject = InvalidRange(Config.empty)

    @Nested
    inner class `check for loop conditions` {

        @Test
        fun `does not report correct bounds in for loop conditions`() {
            val code = """
                fun f() {
                    for (i in 2..2) {}
                    for (i in 2 downTo 2) {}
                    for (i in 2 until 3) {}
                    for (i in 2..<3) {}
                    for (i in 2 until 4 step 2) {}
                    for (i in (1+1)..3) { }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports incorrect bounds in for loop conditions`() {
            val code = """
                fun f() {
                    for (i in 2..1) { }
                    for (i in 1 downTo 2) { }
                    for (i in 2 until 2) { }
                    for (i in 2..<2) { }
                    for (i in 2 until 1 step 2) { }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(5)
        }

        @Test
        fun `reports nested loops with incorrect bounds in for loop conditions`() {
            val code = """
                fun f() {
                    for (i in 2..2) {
                        for (i in 2..1) { }
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `check ranges outside of loops` {

        @Test
        fun `reports for '__'`() {
            val code = "val r = 2..1"
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report binary expressions without an invalid range`() {
            val code = "val sum = 1 + 2"
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
