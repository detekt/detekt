package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForbiddenNameSpec : Spek({

    describe("ForbiddenClassName rule") {
        it("should report classes with forbidden names") {
            val code = """
				class TestManager {} // violation
				class TestProvider {} // violation
				class TestHolder"""
            assertThat(NamingRules(TestConfig(mapOf(ForbiddenClassName.FORBIDDEN_NAME to "Manager ,  Provider")))
                    .lint(code))
                    .hasSize(2)
        }

        it("should report a class that starts with a forbidden name") {
            val code = "class TestProvider {}"
            assertThat(NamingRules(TestConfig(mapOf(ForbiddenClassName.FORBIDDEN_NAME to "test")))
                    .lint(code))
                    .hasSize(1)
        }
    }
})
