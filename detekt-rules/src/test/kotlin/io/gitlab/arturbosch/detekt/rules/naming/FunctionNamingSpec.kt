package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FunctionNamingSpec : Spek({

    describe("FunctionNaming rule") {

        it("allows FunctionName as alias for suppressing") {
            val code = """
            @Suppress("FunctionName")
            fun MY_FUN() = TODO()
        """
            assertThat(FunctionNaming().lint(code)).isEmpty()
        }

        it("ignores functions in classes matching excludeClassPattern") {
            val code = """
            class WhateverTest {
                fun SHOULD_NOT_BE_FLAGGED() = TODO()
            }
        """
            val config = TestConfig(mapOf(FunctionNaming.EXCLUDE_CLASS_PATTERN to ".*Test$"))
            assertThat(FunctionNaming(config).lint(code)).isEmpty()
        }

        it("flags functions inside functions") {
            val code = """
            override fun shouldNotBeFlagged() {
                fun SHOULD_BE_FLAGGED() { }
            }
        """
            assertThat(FunctionNaming().lint(code)).hasSourceLocation(2, 5)
        }

        it("ignores overridden functions by default") {
            val code = """
            override fun SHOULD_NOT_BE_FLAGGED() = TODO()
        """
            assertThat(FunctionNaming().lint(code)).isEmpty()
        }

        it("does not report when the function name is identical to the type of the result") {
            val code = """
            interface Foo
            private class FooImpl : Foo

            fun Foo(): Foo = FooImpl()
        """
            val config = TestConfig(mapOf(FunctionNaming.IGNORE_OVERRIDDEN to "false"))
            assertThat(FunctionNaming(config).lint(code))
        }

        it("flags functions with bad names inside overridden functions by default") {
            val code = """
            override fun SHOULD_NOT_BE_FLAGGED() {
                fun SHOULD_BE_FLAGGED() {}
            }
        """
            assertThat(FunctionNaming().lint(code)).hasSourceLocation(2, 5)
        }

        it("doesn't ignore overridden functions if ignoreOverridden is false") {
            val code = """
            override fun SHOULD_BE_FLAGGED() = TODO()
        """
            val config = TestConfig(mapOf(FunctionNaming.IGNORE_OVERRIDDEN to "false"))
            assertThat(FunctionNaming(config).lint(code)).hasSourceLocation(1, 1)
        }
    }
})
