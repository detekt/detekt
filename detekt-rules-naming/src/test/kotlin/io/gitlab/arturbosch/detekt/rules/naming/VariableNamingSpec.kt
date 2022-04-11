package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class VariableNamingSpec {

    @Test
    fun `should detect all positive cases`() {
        val code = """
            class C {
                private val _FIELD = 5
                val FIELD get() = _FIELD
                val camel_Case_Property = 5
            }
        """
        assertThat(VariableNaming().compileAndLint(code))
            .hasSourceLocations(
                SourceLocation(2, 17),
                SourceLocation(3, 9),
                SourceLocation(4, 9)
            )
    }

    @Test
    fun `checks all negative cases`() {
        val code = """
            class C {
                private val _field = 5
                val field get() = _field
                val camelCaseProperty = 5
            }
        """
        assertThat(VariableNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not flag overridden member properties by default`() {
        val code = """
            class C : I {
                override val SHOULD_NOT_BE_FLAGGED = "banana"
            }
            interface I : I2 {
                override val SHOULD_NOT_BE_FLAGGED: String
            }
            interface I2 {
                @Suppress("VariableNaming") val SHOULD_NOT_BE_FLAGGED: String
            }
        """
        assertThat(VariableNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `doesn't ignore overridden member properties if ignoreOverridden is false`() {
        val code = """
            class C : I {
                override val SHOULD_BE_FLAGGED = "banana"
            }
            interface I : I2 {
                override val SHOULD_BE_FLAGGED: String
            }
            interface I2 {
                @Suppress("VariableNaming") val SHOULD_BE_FLAGGED: String
            }
        """
        val config = TestConfig(mapOf(IGNORE_OVERRIDDEN to "false"))
        assertThat(VariableNaming(config).compileAndLint(code))
            .hasSourceLocations(
                SourceLocation(2, 18),
                SourceLocation(5, 18)
            )
    }
}

private const val IGNORE_OVERRIDDEN = "ignoreOverridden"
