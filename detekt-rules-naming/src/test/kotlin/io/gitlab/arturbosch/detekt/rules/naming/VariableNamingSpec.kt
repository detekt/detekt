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
        """.trimIndent()

        @Test
        fun shouldNotFailWithInvalidRegexWhenDisabledVariableNaming() {
            val config = TestConfig(
                "active" to "false",
                VariableNaming.EXCLUDE_CLASS_PATTERN to "*Foo",
            )
            assertThat(VariableNaming(config).compileAndLint(excludeClassPatternVariableRegexCode)).isEmpty()
        }

        @Test
        fun shouldFailWithInvalidRegexVariableNaming() {
            val config = TestConfig(VariableNaming.EXCLUDE_CLASS_PATTERN to "*Foo")
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
        """.trimIndent()
        val config = TestConfig(VariableNaming.EXCLUDE_CLASS_PATTERN to "Foo|Bar")
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
        """.trimIndent()
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
        """.trimIndent()
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
        """.trimIndent()
        assertThat(VariableNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not flag lambda fun arguments`() {
        val code = """
            fun foo() {
                listOf<Pair<Int, Int>>().flatMap { (left, right) -> listOf(left, right) }
            }
            fun bar() {
                listOf<Pair<Int, Int>>().flatMap { (right, _) -> listOf(right) }
            }
        """.trimIndent()
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
        """.trimIndent()
        val config = TestConfig(IGNORE_OVERRIDDEN to "false")
        assertThat(VariableNaming(config).compileAndLint(code))
            .hasStartSourceLocations(
                SourceLocation(2, 18),
                SourceLocation(5, 18)
            )
    }

    @Test
    fun `should not detect any`() {
        val code = """
            data class D(val i: Int, val j: Int)
            fun doStuff() {
                val (_, HOLY_GRAIL) = D(5, 4)
            }
        """.trimIndent()
        assertThat(VariableNaming().compileAndLint(code)).isEmpty()
    }
}

private const val IGNORE_OVERRIDDEN = "ignoreOverridden"
