package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val VALUES = "values"
private const val ALLOWED_PATTERNS = "allowedPatterns"
private const val MESSAGE = "customMessage"

class ForbiddenCommentSpec {

    val todoColon = "// TODO: I need to fix this."
    val todo = "// TODO I need to fix this."

    val fixmeColon = "// FIXME: I need to fix this."
    val fixme = "// FIXME I need to fix this."

    val stopShipColon = "// STOPSHIP: I need to fix this."
    val stopShip = "// STOPSHIP I need to fix this."

    @Nested
    inner class `the default values are configured` {

        @Test
        @DisplayName("should report TODO: usages")
        fun reportTodoColon() {
            val findings = ForbiddenComment().compileAndLint(todoColon)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report TODO usages`() {
            val findings = ForbiddenComment().compileAndLint(todo)
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("should report FIXME: usages")
        fun reportFixMe() {
            val findings = ForbiddenComment().compileAndLint(fixmeColon)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report FIXME usages`() {
            val findings = ForbiddenComment().compileAndLint(fixme)
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("should report STOPSHIP: usages")
        fun reportStopShipColon() {
            val findings = ForbiddenComment().compileAndLint(stopShipColon)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report STOPSHIP usages`() {
            val findings = ForbiddenComment().compileAndLint(stopShip)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report violation in multiline comment`() {
            val code = """
               /*
                TODO: I need to fix this.
                */
            """
            val findings = ForbiddenComment().compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report violation in KDoc`() {
            val code = """
                /**
                 * TODO: I need to fix this.
                 */
                class A {
                    /**
                     * TODO: I need to fix this.
                     */
                }
            """
            val findings = ForbiddenComment().compileAndLint(code)
            assertThat(findings).hasSize(2)
        }
    }

    @Nested
    inner class `custom default values are configured` {
        val banana = "// Banana."

        @Nested
        inner class `when given Banana` {
            val config = TestConfig(mapOf(VALUES to "Banana"))

            @Test
            @DisplayName("should not report TODO: usages")
            fun todoColon() {
                val findings = ForbiddenComment(config).compileAndLint(todoColon)
                assertThat(findings).isEmpty()
            }

            @Test
            @DisplayName("should not report FIXME: usages")
            fun fixmeColon() {
                val findings = ForbiddenComment(config).compileAndLint(fixmeColon)
                assertThat(findings).isEmpty()
            }

            @Test
            @DisplayName("should not report STOPME: usages")
            fun stopShipColon() {
                val findings = ForbiddenComment(config).compileAndLint(stopShipColon)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `should report Banana usages`() {
                val findings = ForbiddenComment(config).compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should report Banana usages regardless of case sensitive`() {
                val forbiddenComment = ForbiddenComment(TestConfig(mapOf(VALUES to "bAnAnA")))
                val findings = forbiddenComment.compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        @DisplayName("when given listOf(\"banana\")")
        inner class ListOfBanana {
            val config = TestConfig(mapOf(VALUES to listOf("Banana")))

            @Test
            @DisplayName("should not report TODO: usages")
            fun todoColon() {
                val findings = ForbiddenComment(config).compileAndLint(todoColon)
                assertThat(findings).isEmpty()
            }

            @Test
            @DisplayName("should not report FIXME: usages")
            fun fixmeColon() {
                val findings = ForbiddenComment(config).compileAndLint(fixmeColon)
                assertThat(findings).isEmpty()
            }

            @Test
            @DisplayName("should not report STOPME: usages")
            fun stopShipColon() {
                val findings = ForbiddenComment(config).compileAndLint(stopShipColon)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `should report Banana usages`() {
                val findings = ForbiddenComment(config).compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should report Banana usages regardless of case sensitive`() {
                val forbiddenComment = ForbiddenComment(TestConfig(mapOf(VALUES to "bAnAnA")))
                val findings = forbiddenComment.compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }
        }
    }

    @Nested
    inner class `custom default values with allowed patterns are configured` {

        val patternsConfig =
            TestConfig(
                mapOf(
                    VALUES to "Comment",
                    ALLOWED_PATTERNS to "Ticket|Task"
                )
            )

        @Test
        fun `should report Comment usages when regex does not match`() {
            val comment = "// Comment is added here."
            val findings = ForbiddenComment(patternsConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report Comment usages when any one pattern is present`() {
            val comment = "// Comment Ticket:234."
            val findings = ForbiddenComment(patternsConfig).compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report Comment usages when all patterns are present`() {
            val comment = "// Comment Ticket:123 Task:456 comment."
            val findings = ForbiddenComment(patternsConfig).compileAndLint(comment)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `custom message is configured` {
        val messageConfig =
            TestConfig(
                mapOf(
                    VALUES to "Comment",
                    MESSAGE to "Custom Message"
                )
            )

        @Test
        fun `should report a Finding with message 'Custom Message'`() {
            val comment = "// Comment"
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo("Custom Message")
        }
    }

    @Nested
    inner class `custom message is not configured` {
        val messageConfig =
            TestConfig(
                mapOf(
                    VALUES to "Comment"
                )
            )

        @Test
        fun `should report a Finding with default Message`() {
            val comment = "// Comment"
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            val expectedMessage = String.format(ForbiddenComment.DEFAULT_ERROR_MESSAGE, "Comment")
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(expectedMessage)
        }
    }
}
