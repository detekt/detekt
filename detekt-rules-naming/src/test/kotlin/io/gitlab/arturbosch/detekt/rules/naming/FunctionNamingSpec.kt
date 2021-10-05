package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FunctionNamingSpec : Spek({

    describe("FunctionNaming rule") {

        it("allows FunctionName as alias for suppressing") {
            val code = """
            @Suppress("FunctionName")
            fun MY_FUN() {}
        """
            assertThat(FunctionNaming().compileAndLint(code)).isEmpty()
        }

        it("allows anonymous functions") {
            val code = """
            val f: (Int) -> Int = fun(i: Int): Int {
                return i + i
            }
        """
            assertThat(FunctionNaming().compileAndLint(code)).isEmpty()
        }

        it("ignores functions in classes matching excludeClassPattern") {
            val code = """
            class WhateverTest {
                fun SHOULD_NOT_BE_FLAGGED() {}
            }
        """
            val config = TestConfig(mapOf(FunctionNaming.EXCLUDE_CLASS_PATTERN to ".*Test$"))
            assertThat(FunctionNaming(config).compileAndLint(code)).isEmpty()
        }

        it("flags functions inside functions") {
            val code = """
            class C : I {
                override fun shouldNotBeFlagged() {
                    fun SHOULD_BE_FLAGGED() { }
                }
            }
            interface I { fun shouldNotBeFlagged() }
        """
            assertThat(FunctionNaming().compileAndLint(code)).hasSourceLocation(3, 13)
        }

        it("ignores overridden functions by default") {
            val code = """
            class C : I {
                override fun SHOULD_NOT_BE_FLAGGED() {}
            }
            interface I { @Suppress("FunctionNaming") fun SHOULD_NOT_BE_FLAGGED() }
        """
            assertThat(FunctionNaming().compileAndLint(code)).isEmpty()
        }

        it("does not report when the function name is identical to the type of the result") {
            val code = """
            interface Foo
            private class FooImpl : Foo

            fun Foo(): Foo = FooImpl()
        """
            val config = TestConfig(mapOf(FunctionNaming.IGNORE_OVERRIDDEN to "false"))
            assertThat(FunctionNaming(config).compileAndLint(code)).isEmpty()
        }

        it("flags functions with bad names inside overridden functions by default") {
            val code = """
            class C : I {
                override fun SHOULD_BE_FLAGGED() {
                    fun SHOULD_BE_FLAGGED() {}
                }
            }
            interface I { @Suppress("FunctionNaming") fun SHOULD_BE_FLAGGED() }
        """
            assertThat(FunctionNaming().compileAndLint(code)).hasSourceLocation(3, 13)
        }

        it("doesn't ignore overridden functions if ignoreOverridden is false") {
            val code = """
            class C : I {
                override fun SHOULD_BE_FLAGGED() {}
            }
            interface I { fun SHOULD_BE_FLAGGED() }
        """
            val config = TestConfig(mapOf(FunctionNaming.IGNORE_OVERRIDDEN to "false"))
            assertThat(FunctionNaming(config).compileAndLint(code)).hasSourceLocations(
                SourceLocation(2, 18),
                SourceLocation(4, 19)
            )
        }

        it("allow functions with backtick") {
            val code = """
                fun `7his is a function name _`() = Unit
            """
            assertThat(FunctionNaming().compileAndLint(code)).isEmpty()
        }
    }
})
