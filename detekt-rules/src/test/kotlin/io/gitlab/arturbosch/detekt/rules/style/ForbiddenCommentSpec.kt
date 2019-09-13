package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForbiddenCommentSpec : Spek({

    val todoColon = "// TODO: I need to fix this."
    val todo = "// TODO I need to fix this."

    val fixmeColon = "// FIXME: I need to fix this."
    val fixme = "// FIXME I need to fix this."

    val stopShipColon = "// STOPSHIP: I need to fix this."
    val stopShip = "// STOPSHIP I need to fix this."

    describe("ForbiddenComment rule") {

        context("the default values are configured") {

            it("should report TODO: usages") {
                val findings = ForbiddenComment().compileAndLint(todoColon)
                assertThat(findings).hasSize(1)
            }

            it("should not report TODO usages") {
                val findings = ForbiddenComment().compileAndLint(todo)
                assertThat(findings).hasSize(0)
            }

            it("should report FIXME: usages") {
                val findings = ForbiddenComment().compileAndLint(fixmeColon)
                assertThat(findings).hasSize(1)
            }

            it("should not report FIXME usages") {
                val findings = ForbiddenComment().compileAndLint(fixme)
                assertThat(findings).hasSize(0)
            }

            it("should report STOPSHIP: usages") {
                val findings = ForbiddenComment().compileAndLint(stopShipColon)
                assertThat(findings).hasSize(1)
            }

            it("should not report STOPSHIP usages") {
                val findings = ForbiddenComment().compileAndLint(stopShip)
                assertThat(findings).hasSize(0)
            }
        }

        context("custom default values are configured") {
            val banana = "// Banana."
            val config = TestConfig(mapOf(ForbiddenComment.VALUES to "Banana"))

            it("should not report TODO: usages") {
                val findings = ForbiddenComment(config).compileAndLint(todoColon)
                assertThat(findings).hasSize(0)
            }

            it("should not report FIXME: usages") {
                val findings = ForbiddenComment(config).compileAndLint(fixmeColon)
                assertThat(findings).hasSize(0)
            }

            it("should not report STOPME: usages") {
                val findings = ForbiddenComment(config).compileAndLint(stopShipColon)
                assertThat(findings).hasSize(0)
            }

            it("should report Banana usages") {
                val findings = ForbiddenComment(config).compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }

            it("should report Banana usages regardless of case sensitive") {
                val forbiddenComment = ForbiddenComment(TestConfig(mapOf(ForbiddenComment.VALUES to "bAnAnA")))
                val findings = forbiddenComment.compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }
        }

        context("custom default values with allowed patterns are configured") {
            val patternsConfig = TestConfig(
                mapOf(
                    ForbiddenComment.VALUES to "Comment",
                    ForbiddenComment.ALLOWED_PATTERNS to "Ticket|Task"
                )
            )

            it("should report Comment usages when regex does not match") {
                val comment = "// Comment is added here."
                val findings = ForbiddenComment(patternsConfig).compileAndLint(comment)
                assertThat(findings).hasSize(1)
            }
            it("should not report Comment usages when any one pattern is present") {
                val comment = "// Comment Ticket:234."
                val findings = ForbiddenComment(patternsConfig).compileAndLint(comment)
                assertThat(findings).isEmpty()
            }

            it("should not report Comment usages when all patterns are present") {
                val comment = "// Comment Ticket:123 Task:456 comment."
                val findings = ForbiddenComment(patternsConfig).compileAndLint(comment)
                assertThat(findings).isEmpty()
            }

        }
    }
})
