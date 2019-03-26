package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
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
    }
})
