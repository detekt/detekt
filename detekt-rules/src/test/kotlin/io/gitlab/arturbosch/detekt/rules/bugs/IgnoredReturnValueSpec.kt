package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object IgnoredReturnValueSpec : Spek({

    val subject by memoized { IgnoredReturnValue() }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("reports scenarios") {
        it("reports when a function which returns a value is called and the return is ignored") {
            val code = """
                fun foo() {
                    listOf("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 5)
        }

        it("reports when a function which returns a value is called before a valid return") {
            val code = """
                fun foo() : Int {
                    listOf("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 5)
        }

        it("reports when a function which returns a value is called in chain and the return is ignored") {
            val code = """
                fun foo() {
                    listOf("hello").isEmpty().not()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 31)
        }

        it("reports when a function which returns a value is called before a semicolon") {
            val code = """
                fun foo() {
                    listOf("hello");println("foo")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 5)
        }

        it("reports when a function which returns a value is called after a semicolon") {
            val code = """
                fun foo() {
                    println("foo");listOf("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 20)
        }

        it("reports when a function which returns a value is called before a comment") {
            val code = """
                fun foo() {
                    listOf("hello")//foo
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 5)
        }

        it("reports when a function which returns a value is called after a semicolon") {
            val code = """
                fun foo() {
                    /* foo */listOf("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 14)
        }


        it("reports when an extension function which returns a value is called and the return is ignored") {
            val code = """
                fun Int.isTheAnswer(): Boolean = this == 42
                fun foo(input: Int) {
                    input.isTheAnswer()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(3, 11)
        }
    }

    describe("empty scenarios") {
        it("does not report when the return value is assigned to a pre-existing variable") {
            val code = """
                fun foo() {
                    var x: List<String>
                    x = listOf("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which doesn't return a value is called") {
            val code = """
                fun noReturnValue() {}

                fun foo() {
                    noReturnValue()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used in a test statement") {
            val code = """
                fun returnsBoolean() = true
                
                if (returnsBoolean()) {
                    // no-op
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used in a comparison") {
            val code = """
                fun returnsInt() = 42
                
                if (42 == returnsInt()) {
                    // no-op
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used as parameter for another call") {
            val code = """
                fun returnsInt() = 42
                
                println(returnsInt())
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used with named paramters") {
            val code = """
                fun returnsInt() = 42
                
                println(message = returnsInt())
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }
    }

})
