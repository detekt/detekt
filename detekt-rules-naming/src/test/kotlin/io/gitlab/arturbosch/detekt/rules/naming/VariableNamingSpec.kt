package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
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
        fun shouldFailWithInvalidRegexVariableNaming() {
            val config = TestConfig(VariableNaming.EXCLUDE_CLASS_PATTERN to "*Foo")
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                VariableNaming(config).lint(excludeClassPatternVariableRegexCode)
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
        assertThat(VariableNaming(config).lint(code)).isEmpty()
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
        val findings = VariableNaming(Config.empty).lint(code)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasStartSourceLocation(2, 17) },
            { assertThat(it).hasStartSourceLocation(3, 9) },
            { assertThat(it).hasStartSourceLocation(4, 9) },
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
        assertThat(VariableNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not flag overridden member properties`() {
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
        assertThat(VariableNaming(Config.empty).lint(code)).isEmpty()
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
        assertThat(VariableNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not detect any`() {
        val code = """
            data class D(val i: Int, val j: Int)
            fun doStuff() {
                val (_, HOLY_GRAIL) = D(5, 4)
            }
        """.trimIndent()
        assertThat(VariableNaming(Config.empty).lint(code)).isEmpty()
    }
}
