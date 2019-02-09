package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class ForbiddenCommentSpec : Spek({

    val todoColon = "// TODO: I need to fix this."
    val todo = "// TODO I need to fix this."

    val fixmeColon = "// FIXME: I need to fix this."
    val fixme = "// FIXME I need to fix this."

    val stopShipColon = "// STOPSHIP: I need to fix this."
    val stopShip = "// STOPSHIP I need to fix this."

    given("the default values are configured") {

        it("should report TODO: usages") {
            val findings = ForbiddenComment().lint(todoColon)
            assertThat(findings).hasSize(1)
        }

        it("should not report TODO usages") {
            val findings = ForbiddenComment().lint(todo)
            assertThat(findings).hasSize(0)
        }

        it("should report FIXME: usages") {
            val findings = ForbiddenComment().lint(fixmeColon)
            assertThat(findings).hasSize(1)
        }

        it("should not report FIXME usages") {
            val findings = ForbiddenComment().lint(fixme)
            assertThat(findings).hasSize(0)
        }

        it("should report STOPSHIP: usages") {
            val findings = ForbiddenComment().lint(stopShipColon)
            assertThat(findings).hasSize(1)
        }

        it("should not report STOPSHIP usages") {
            val findings = ForbiddenComment().lint(stopShip)
            assertThat(findings).hasSize(0)
        }
    }

    given("custom default values are configured") {
        val banana = "// Banana."

        it("should not report TODO: usages") {
            val findings = ForbiddenComment(TestConfig(mapOf(ForbiddenComment.VALUES to "Banana"))).lint(todoColon)
            assertThat(findings).hasSize(0)
        }

        it("should not report FIXME: usages") {
            val findings = ForbiddenComment(TestConfig(mapOf(ForbiddenComment.VALUES to "Banana"))).lint(fixmeColon)
            assertThat(findings).hasSize(0)
        }

        it("should not report STOPME: usages") {
            val findings = ForbiddenComment(TestConfig(mapOf(ForbiddenComment.VALUES to "Banana"))).lint(stopShipColon)
            assertThat(findings).hasSize(0)
        }

        it("should report Banana usages") {
            val findings = ForbiddenComment(TestConfig(mapOf(ForbiddenComment.VALUES to "Banana"))).lint(banana)
            assertThat(findings).hasSize(1)
        }

        it("should report Banana usages regardless of case sensitive") {
            val findings = ForbiddenComment(TestConfig(mapOf(ForbiddenComment.VALUES to "bAnAnA"))).lint(banana)
            assertThat(findings).hasSize(1)
        }
    }
})
