package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class FunctionNamingSpec {

    @Test
    fun `allows FunctionName as alias for suppressing`() {
        val code = """
        @Suppress("FunctionName")
        fun MY_FUN() {}
        """.trimIndent()
        assertThat(FunctionNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `allows anonymous functions`() {
        val code = """
        val f: (Int) -> Int = fun(i: Int): Int {
            return i + i
        }
        """.trimIndent()
        assertThat(FunctionNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `ignores functions in classes matching excludeClassPattern`() {
        val code = """
        class WhateverTest {
            fun SHOULD_NOT_BE_FLAGGED() {}
        }
        """.trimIndent()
        val config = TestConfig(mapOf(FunctionNaming.EXCLUDE_CLASS_PATTERN to ".*Test$"))
        assertThat(FunctionNaming(config).compileAndLint(code)).isEmpty()
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
        assertThat(FunctionNaming().compileAndLint(code)).hasStartSourceLocation(3, 13)
    }

    @Test
    fun `ignores overridden functions by default`() {
        val code = """
        class C : I {
            override fun SHOULD_NOT_BE_FLAGGED() {}
        }
        interface I { @Suppress("FunctionNaming") fun SHOULD_NOT_BE_FLAGGED() }
        """.trimIndent()
        assertThat(FunctionNaming().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report when the function name is identical to the type of the result`() {
        val code = """
        interface Foo
        private class FooImpl : Foo

        fun Foo(): Foo = FooImpl()
        """.trimIndent()
        val config = TestConfig(mapOf(FunctionNaming.IGNORE_OVERRIDDEN to "false"))
        assertThat(FunctionNaming(config).compileAndLint(code)).isEmpty()
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
        assertThat(FunctionNaming().compileAndLint(code)).hasStartSourceLocation(3, 13)
    }

    @Test
    fun `doesn't ignore overridden functions if ignoreOverridden is false`() {
        val code = """
        class C : I {
            override fun SHOULD_BE_FLAGGED() {}
        }
        interface I { fun SHOULD_BE_FLAGGED() }
        """.trimIndent()
        val config = TestConfig(mapOf(FunctionNaming.IGNORE_OVERRIDDEN to "false"))
        assertThat(FunctionNaming(config).compileAndLint(code)).hasStartSourceLocations(
            SourceLocation(2, 18),
            SourceLocation(4, 19)
        )
    }

    @Test
    fun `doesn't allow functions with backtick`() {
        val code = """
            fun `7his is a function name _`() = Unit
        """.trimIndent()
        assertThat(FunctionNaming().compileAndLint(code)).hasStartSourceLocations(SourceLocation(1, 5))
    }
}
