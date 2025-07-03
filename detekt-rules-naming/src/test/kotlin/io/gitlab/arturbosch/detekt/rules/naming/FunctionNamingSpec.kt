package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

@KotlinCoreEnvironmentTest
class FunctionNamingSpec(val env: KotlinEnvironmentContainer) {

    @Test
    fun `allows FunctionName for suppressing`() {
        val code = """
            @Suppress("FunctionNaming")
            fun MY_FUN() {}
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `allows anonymous functions`() {
        val code = """
            val f: (Int) -> Int = fun(i: Int): Int {
                return i + i
            }
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `ignores functions in classes matching excludeClassPattern`() {
        val code = """
            class WhateverTest {
                fun SHOULD_NOT_BE_FLAGGED() {}
            }
        """.trimIndent()
        val config = TestConfig(FunctionNaming.EXCLUDE_CLASS_PATTERN to ".*Test$")
        assertThat(FunctionNaming(config).lint(code)).isEmpty()
    }

    @Test
    fun `flags functions inside functions`() {
        val code = """
            class C : I {
                override fun shouldNotBeFlagged() {
                    fun SHOULD_BE_FLAGGED() { }
                }
            }
            interface I { fun shouldNotBeFlagged() }
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).hasStartSourceLocation(3, 13)
    }

    @Test
    fun `ignores overridden functions`() {
        val code = """
            class C : I {
                override fun SHOULD_NOT_BE_FLAGGED() {}
            }
            interface I { @Suppress("FunctionNaming") fun SHOULD_NOT_BE_FLAGGED() }
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `does not report when the function name is identical to the type of the result`() {
        val code = """
            interface Foo
            private class FooImpl : Foo
            
            fun Foo(): Foo = FooImpl()
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `does not report when the function's name equals to the return type's name with type arguments`() {
        val code = """
            interface Foo<T>
            fun <T> Foo(): Foo<T> = object : Foo<T> {}
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `flags functions with bad names inside overridden functions by default`() {
        val code = """
            class C : I {
                override fun SHOULD_BE_FLAGGED() {
                    fun SHOULD_BE_FLAGGED() {}
                }
            }
            interface I { @Suppress("FunctionNaming") fun SHOULD_BE_FLAGGED() }
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).hasStartSourceLocation(3, 13)
    }

    @Test
    fun `doesn't allow functions with backtick`() {
        val code = """
            fun `7his is a function name _`() = Unit
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).hasStartSourceLocations(SourceLocation(1, 5))
    }

    @Test
    fun `should use custom name for method`() {
        val code = """
            class Foo {
                fun `name with back ticks`() {
                }
            }
        """.trimIndent()
        val config = TestConfig(FunctionNaming.FUNCTION_PATTERN to "^`.+`$")
        assertThat(FunctionNaming(config).lint(code)).isEmpty()
    }

    @Test
    fun shouldExcludeClassesFromFunctionNaming() {
        val code = """
            class Bar {
                fun MYFun() {}
            }
            
            object Foo {
                fun MYFun() {}
            }
        """.trimIndent()
        val config = TestConfig(FunctionNaming.EXCLUDE_CLASS_PATTERN to "Foo|Bar")
        assertThat(FunctionNaming(config).lint(code)).isEmpty()
    }

    @Test
    fun `should report a function name that begins with a backtick, capitals, and spaces`() {
        val subject = FunctionNaming(Config.empty)
        val code = "fun `Hi bye`() = 3"
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Nested
    inner class `exclude class pattern function regex code cases` {
        private val excludeClassPatternFunctionRegexCode = """
            class Bar {
                fun MYFun() {}
            }
            
            object Foo {
                fun MYFun() {}
            }
        """.trimIndent()

        @Test
        fun shouldFailWithInvalidRegexFunctionNaming() {
            val config = TestConfig(FunctionNaming.EXCLUDE_CLASS_PATTERN to "*Foo")
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                FunctionNaming(config).lint(excludeClassPatternFunctionRegexCode)
            }
        }
    }

    @Test
    fun `should not detect any`() {
        val code = """
            data class D(val i: Int, val j: Int)
            fun doStuff() {
                val (_, HOLY_GRAIL) = D(5, 4)
            }
        """.trimIndent()
        assertThat(FunctionNaming(Config.empty).lint(code)).isEmpty()
    }
}
