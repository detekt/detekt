package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.FinalNewline
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import org.junit.jupiter.api.Test

class FinalNewlineSpec {

    @Test
    fun `should report missing new line by default`() {
        val findings = FinalNewline(Config.empty)
            .lint("fun main() = Unit")

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report as new line is present`() {
        val findings = FinalNewline(Config.empty).lint(
            """
                fun main() = Unit

            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report new line when configured`() {
        val findings = FinalNewline(TestConfig(INSERT_FINAL_NEWLINE_KEY to "false"))
            .lint(
                """
                    fun main() = Unit

                """.trimIndent()
            )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report when no new line is configured and not present`() {
        val findings = FinalNewline(TestConfig(INSERT_FINAL_NEWLINE_KEY to "false"))
            .lint("fun main() = Unit")

        assertThat(findings).isEmpty()
    }
}

private const val INSERT_FINAL_NEWLINE_KEY = "insertFinalNewLine"
