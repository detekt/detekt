package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EnumNamingSpec : Spek({

    describe("some enum entry declarations") {

        it("should detect no violation") {
            val findings = NamingRules().compileAndLint(
                    """
                enum class WorkFlow {
                    ACTIVE, NOT_ACTIVE, Unknown, Number1
                }
                """
            )
            assertThat(findings).isEmpty()
        }

        it("reports an underscore in enum name") {
            val code = """
                enum class WorkFlow {
                    _Default
                }"""
            assertThat(NamingRules().compileAndLint(code)).hasSize(1)
        }

        it("no reports an underscore in enum name because it's suppressed") {
            val code = """
                enum class WorkFlow {
                    @Suppress("EnumNaming") _Default
                }"""
            assertThat(NamingRules().compileAndLint(code)).isEmpty()
        }

        it("reports the correct text location in enum name") {
            val code = """
                enum class WorkFlow {
                    _Default,
                }"""
            val findings = NamingRules().compileAndLint(code)
            val source = findings.first().location.text
            assertThat(source.start).isEqualTo(26)
            assertThat(source.end).isEqualTo(34)
        }
    }
})
