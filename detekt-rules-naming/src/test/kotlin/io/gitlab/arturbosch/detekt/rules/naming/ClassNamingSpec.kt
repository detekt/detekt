package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class ClassNamingSpec {

    @Test
    fun `should use custom name for method and class`() {
        val config = TestConfig(mapOf(ClassNaming.CLASS_PATTERN to "^aBbD$"))
        assertThat(
            ClassNaming(config).compileAndLint(
                """
        class aBbD{}
                """
            )
        ).isEmpty()
    }

    @Test
    fun `should detect no violations class with numbers`() {
        val code = """
            class MyClassWithNumbers5
        """

        assertThat(ClassNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should detect no violations`() {
        val code = """
            class NamingConventions {
            }
        """

        assertThat(ClassNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should detect no violations with class using backticks`() {
        val code = """
            class `NamingConventions`
        """

        assertThat(ClassNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should detect because it have a _`() {
        val code = """
            class _NamingConventions
        """

        assertThat(ClassNaming().compileAndLint(code))
            .hasSize(1)
            .hasTextLocations(6 to 24)
    }

    @Test
    fun `should detect because it have starts with lowercase`() {
        val code = """
            class namingConventions {}
        """

        assertThat(ClassNaming().compileAndLint(code))
            .hasSize(1)
            .hasTextLocations(6 to 23)
    }

    @Test
    fun `should ignore the issue by alias suppression`() {
        val code = """
            @Suppress("ClassName")
            class namingConventions {}
        """
        assertThat(ClassNaming().compileAndLint(code)).isEmpty()
    }
}
