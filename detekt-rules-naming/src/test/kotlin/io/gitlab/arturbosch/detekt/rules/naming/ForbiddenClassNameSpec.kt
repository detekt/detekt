package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.rules.naming.ForbiddenClassName.Companion.FORBIDDEN_NAME
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForbiddenClassNameSpec : Spek({

    describe("ForbiddenClassName rule") {

        it("should report classes with forbidden names") {
            val code = """
                class TestManager {} // violation
                class TestProvider {} // violation
                class TestHolder"""
            assertThat(ForbiddenClassName(TestConfig(mapOf(FORBIDDEN_NAME to listOf("Manager", "Provider"))))
                    .compileAndLint(code))
                    .hasSize(2)
        }

        it("should report a class that starts with a forbidden name") {
            val code = "class TestProvider {}"
            assertThat(ForbiddenClassName(TestConfig(mapOf(FORBIDDEN_NAME to listOf("test"))))
                    .compileAndLint(code))
                    .hasSize(1)
        }

        it("should report classes with forbidden names using config string") {
            val code = """
                class TestManager {} // violation
                class TestProvider {} // violation
                class TestHolder"""
            assertThat(ForbiddenClassName(TestConfig(mapOf(FORBIDDEN_NAME to "Manager, Provider")))
                .compileAndLint(code))
                .hasSize(2)
        }
    }
})
