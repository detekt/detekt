package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

class VariableNamingSpec {

    @Nested
    inner class `Exclude class pattern cases` {

        private val excludeClassPatternVariableRegexCode = """
            class Bar {
                val MYVar = 3
            }

            object Foo {
                val MYVar = 3
            }
    """

        @Test
        fun shouldNotFailWithInvalidRegexWhenDisabledVariableNaming() {
            val configValues = mapOf(
                "active" to "false",
                VariableNaming.EXCLUDE_CLASS_PATTERN to "*Foo"
            )
            val config = TestConfig(configValues)
            assertThat(VariableNaming(config).compileAndLint(excludeClassPatternVariableRegexCode)).isEmpty()
        }

        @Test
        fun shouldFailWithInvalidRegexVariableNaming() {
            val config = TestConfig(mapOf(VariableNaming.EXCLUDE_CLASS_PATTERN to "*Foo"))
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                VariableNaming(config).compileAndLint(excludeClassPatternVariableRegexCode)
            }
        }
    }

    @Test
    fun shouldExcludeClassesFromVariableNaming() {
        val code = """
        class Bar {
            val MYVar = 3
        }

        object Foo {
            val MYVar = 3
        }
        """
        val config = TestConfig(mapOf(VariableNaming.EXCLUDE_CLASS_PATTERN to "Foo|Bar"))
        assertThat(VariableNaming(config).compileAndLint(code)).isEmpty()
    }

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
            .hasStartSourceLocations(
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
    fun `should not flag global member properties`() {
        val code = """
            const val DEFAULT_FLOAT_CONVERSION_FACTOR: Int = 100
            private const val QUOTES = "\""
        """
        assertThat(VariableNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not flag lambda fun arguments`() {
        val code = """
            fun foo() {
                listOf<Pair<Int, Int>>().flatMap { (test, left) -> print("H") }
            }
            fun bar() {
                listOf<Pair<Int, Int>>().flatMap { (right, _) -> print("H") }
            }
        """
        assertThat(VariableNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not flag member properties into companion object`() {
        val code = """
            class Foo {
                companion object {
        val TWENTY_MINS: Debt =
            Debt(0, 0, 20)
        val TEN_MINS: Debt =
            Debt(0, 0, 10)
        val FIVE_MINS: Debt =
            Debt(0, 0, 5)
        private const val HOURS_PER_DAY = 24
        private const val MINUTES_PER_HOUR = 60

        const val ACTIVE_KEY: String = "active"
    }
            }
        """
        assertThat(VariableNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not try catch member properties into companion object`() {
        val code = """
            fun Config.valueOrDefaultCommaSeparated(
                key: String,
                default: List<String>
            ): List<String> {
                fun fallBack() = valueOrDefault(key, default.joinToString(","))
                    .trim()
                    .commaSeparatedPattern(",", ";")
                    .toList()
            
                return try {
                    valueOrDefault(key, default)
                } catch (_: IllegalStateException) {
                    fallBack()
                } catch (_: ClassCastException) {
                    fallBack()
                }
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
            .hasStartSourceLocations(
                SourceLocation(2, 18),
                SourceLocation(5, 18)
            )
    }
}

private const val IGNORE_OVERRIDDEN = "ignoreOverridden"
