package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CascadingCallWrappingSpec {
    private val subject = CascadingCallWrapping(Config.empty)

    @Test
    fun `reports missing wrapping`() {
        val code = """
            val a = 0
                .plus(0).plus(0).plus(0)
        """.trimIndent()

        assertThat(subject.lint(code))
            .hasSize(1)
            .hasTextLocations(23 to 30)
            .first()
            .hasMessage("Chained call `plus(0)` should be wrapped to a new line since preceding calls were.")
    }

    @Test
    fun `does not report when chained calls are on a single line`() {
        val code = """
            val a = 0.plus(0).plus(0)
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report wrapped calls`() {
        val code = """
            val a = 0
                .plus(0)
                .plus(0)
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report unwrapped initial calls`() {
        val code = """
            val a = 0.plus(0).plus(0)
                .plus(0)
                .plus(0)
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports missing wrapping for safe qualified calls`() {
        val code = """
            val a = 0
                ?.plus(0)?.plus(0)
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports missing wrapping for calls with non-null assertions`() {
        val code = """
            val a = 0!!
                .plus(0)!!.plus(0)
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports missing wrapping for properties`() {
        val code = """
            val a = ""
                .plus("").length
            
            val b = ""
                .length.plus(0)
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(2)
    }

    @Nested
    inner class `with multiline calls` {
        @Test
        fun `does not report with wrapping`() {
            val code = """
                val a = 0
                    .plus(
                        0
                    )
                    .let {
                        0
                    }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports missing wrapping`() {
            val code = """
                val a = 0
                    .plus(
                        0
                    )
                    .let {
                        0
                    }.plus(
                        0
                    )
            """.trimIndent()

            assertThat(subject.lint(code))
                .hasTextLocations(64 to 85)
                .hasSize(1)
        }

        @Test
        fun `does not report when calls are multiline but never wrapped`() {
            val code = """
                val a = 0.plus(
                    0
                ).let {
                    0
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report for single multiline call`() {
            val code = """
                val a = 0.plus(
                    0
                )
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `with elvis operators` {
        private val subjectIncludingElvis = CascadingCallWrapping(TestConfig("includeElvis" to true))
        private val subjectExcludingElvis = CascadingCallWrapping(TestConfig("includeElvis" to false))

        @Test
        fun `does not report with wrapping`() {
            val code = """
                val a = 0
                    .plus(0)
                    ?: 0
            """.trimIndent()

            assertThat(subjectIncludingElvis.lint(code)).isEmpty()
            assertThat(subjectExcludingElvis.lint(code)).isEmpty()
        }

        @Test
        fun `reports missing wrapping`() {
            val code = """
                val a = 0
                    .plus(0) ?: 42
            """.trimIndent()

            assertThat(subjectIncludingElvis.lint(code))
                .hasTextLocations(23 to 28)
                .hasSize(1)
            assertThat(subjectExcludingElvis.lint(code)).isEmpty()
        }

        @Test
        fun `reports missing wrapping multiline call`() {
            val code = """
                val a = 0
                    .plus(0) ?: run {
                  42
                }
            """.trimIndent()

            assertThat(subjectIncludingElvis.lint(code))
                .hasTextLocations(23 to 38)
                .hasSize(1)
            assertThat(subjectExcludingElvis.lint(code)).isEmpty()
        }
    }
}
