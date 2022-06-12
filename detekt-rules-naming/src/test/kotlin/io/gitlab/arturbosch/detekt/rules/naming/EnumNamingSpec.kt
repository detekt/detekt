package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class EnumNamingSpec {

    @Test
    fun `should detect no violation`() {
        val findings = EnumNaming().compileAndLint(
            """
            enum class WorkFlow {
                ACTIVE, NOT_ACTIVE, Unknown, Number1
            }
            """
        )
        assertThat(findings).isEmpty()
    }

    @Test
    fun `enum name that start with lowercase`() {
        val code = """
            enum class WorkFlow {
                default
            }
        """
        assertThat(NamingRules().compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports an underscore in enum name`() {
        val code = """
            enum class WorkFlow {
                _Default
            }
        """
        assertThat(EnumNaming().compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `no reports an underscore in enum name because it's suppressed`() {
        val code = """
            enum class WorkFlow {
                @Suppress("EnumNaming") _Default
            }
        """
        assertThat(EnumNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports the correct text location in enum name`() {
        val code = """
            enum class WorkFlow {
                _Default,
            }
        """
        val findings = EnumNaming().compileAndLint(code)
        assertThat(findings).hasTextLocations(26 to 34)
    }
}
