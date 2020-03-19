package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EnumNamingSpec : Spek({

    describe("some enum entry declarations") {

        it("should detect no violation") {
            val findings = EnumNaming().compileAndLint(
                    """
                enum class WorkFlow {
                    ACTIVE, NOT_ACTIVE, Unknown, Number1
                }
                """
            )
            assertThat(findings).isEmpty()
        }

        it("enum name that start with lowercase") {
            val code = """
                enum class WorkFlow {
                    default
                }"""
            assertThat(NamingRules().compileAndLint(code)).hasSize(1)
        }

        it("reports an underscore in enum name") {
            val code = """
                enum class WorkFlow {
                    _Default
                }"""
            assertThat(EnumNaming().compileAndLint(code)).hasSize(1)
        }

        it("no reports an underscore in enum name because it's suppressed") {
            val code = """
                enum class WorkFlow {
                    @Suppress("EnumNaming") _Default
                }"""
            assertThat(EnumNaming().compileAndLint(code)).isEmpty()
        }

        it("reports the correct text location in enum name") {
            val code = """
                enum class WorkFlow {
                    _Default,
                }"""
            val findings = EnumNaming().compileAndLint(code)
            assertThat(findings).hasTextLocations(26 to 34)
        }
    }
})
