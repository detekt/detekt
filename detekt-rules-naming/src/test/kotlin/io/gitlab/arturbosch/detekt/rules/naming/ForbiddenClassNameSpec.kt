package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

private const val FORBIDDEN_NAME = "forbiddenName"

@KotlinCoreEnvironmentTest
class ForbiddenClassNameSpec(val env: KotlinEnvironmentContainer) {

    @Test
    fun `should report a class that starts with a forbidden name`() {
        val code = "class TestProvider {}"
        assertThat(
            ForbiddenClassName(TestConfig(FORBIDDEN_NAME to listOf("test")))
                .lint(code)
        ).hasSize(1)
    }

    @Test
    fun `should report classes with forbidden names using config string using wildcards`() {
        val code = """
            class TestManager {} // violation
            class TestProvider {} // violation
            class TestHolder
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
        val actual = ForbiddenClassName(TestConfig(FORBIDDEN_NAME to listOf("Test", "Manager", "Provider")))
            .lint(code)
        assertThat(actual.first().message)
            .isEqualTo("Class name TestManager is forbidden as it contains: Test, Manager")
    }
}
