package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.FinalNewline
import io.gitlab.arturbosch.detekt.formatting.wrappers.INSERT_FINAL_NEWLINE
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FinalNewlineSpec : Spek({

    describe("FinalNewline rule") {

        it("should report missing new line by default") {
            val findings = FinalNewline(Config.empty)
                .lint("fun main() = Unit".trimIndent())

            assertThat(findings).hasSize(1)
        }

        it("should not report as new line is present") {
            val findings = FinalNewline(Config.empty).lint("""
                    fun main() = Unit

            """.trimIndent())

            assertThat(findings).isEmpty()
        }

        it("should report new line when configured") {
            val findings = FinalNewline(TestConfig(INSERT_FINAL_NEWLINE to "false"))
                .lint("""
                fun main() = Unit

            """.trimIndent())

            assertThat(findings).hasSize(1)
        }

        it("should not report when no new line is configured and not present") {
            val findings = FinalNewline(TestConfig(INSERT_FINAL_NEWLINE to "false"))
                .lint("fun main() = Unit".trimIndent())

            assertThat(findings).isEmpty()
        }
    }
})
