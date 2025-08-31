package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

private const val FORBIDDEN_NAME = "forbiddenName"

class ForbiddenClassNameSpec {

    @Test
    fun `should report classes with exactly matching forbidden names`() {
        val code = """
            class Manager {} // violation
            class Provider {} // violation
            class Holder
        """.trimIndent()
        assertThat(ForbiddenClassName(TestConfig(FORBIDDEN_NAME to listOf("Manager", "Provider"))).lint(code))
            .hasSize(2)
    }

    @Test
    fun `should treat patterns as case-sensitive`() {
        val code = """
            class Manager {} // allowed, since it doesn't match the rule's capitalization
            class Provider {} // violation
            class Holder
        """.trimIndent()
        assertThat(ForbiddenClassName(TestConfig(FORBIDDEN_NAME to listOf("manager", "Provider"))).lint(code))
            .singleElement()
            .matches { it.message == "Class name Provider is forbidden as it matches: Provider" }
    }

    @Test
    fun `should report a class that starts with a forbidden name`() {
        val code = "class TestProvider {}"
        assertThat(ForbiddenClassName(TestConfig(FORBIDDEN_NAME to listOf("Test*"))).lint(code)).hasSize(1)
    }

    @Test
    fun `should report classes with forbidden names using config string using wildcards`() {
        val code = """
            class TestManagerUtil {} // violation
            class Provider {} // violation
            class TestHolderUtil
        """.trimIndent()
        assertThat(
            ForbiddenClassName(TestConfig(FORBIDDEN_NAME to listOf("*Manager*", "*Provider*")))
                .lint(code)
        ).hasSize(2)
    }

    @Test
    fun `should report all forbidden names in message`() {
        val code = """
            class TestManager {}
        """.trimIndent()
        val actual = ForbiddenClassName(TestConfig(FORBIDDEN_NAME to listOf("Test*", "*Manager", "Provider")))
            .lint(code)
        assertThat(actual).singleElement()
            .hasMessage("Class name TestManager is forbidden as it matches: Test.*, .*Manager")
    }

    /**
     * Before 2.0.0, forbidden names were treated unconditionally as substrings;
     * this test checks that is no longer the case.
     */
    @Test
    fun `does not treat pattern as a substring`() {
        val code = """
            class TestManagerUtil {}
        """.trimIndent()
        assertThat(ForbiddenClassName(TestConfig(FORBIDDEN_NAME to listOf("Manager"))).lint(code)).isEmpty()
    }
}
