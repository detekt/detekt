package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object IgnoredReturnValueSpec : Spek({

    val subject by memoized { IgnoredReturnValue() }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("IgnoredReturnValue rule") {
        it("reports when a function which returns a value is called and the return is ignored") {
            val code = """
                fun foo() {
                    listOf("hello")
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports when an extension function which returns a value is called and the return is ignored") {
            val code = """
                fun Byte.setLastBit(): Byte = this or 0x1
                fun foo(b: Byte) {
                    b.setLastBit()
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("does not report when the return value is assigned to a pre-existing variable") {
            val code = """
                fun foo() {
                    var x: List<String>
                    x = listOf("hello")
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(0)
        }

        it("does not report when a function which doesn't return a value is called") {
            val code = """
                fun noReturnValue() {}

                fun foo() {
                    noReturnValue()
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(0)
        }

        it("does not report when a function's return value is used in a test statement") {
            val code = """
                fun returnsBoolean() = true
                
                if (returnsBoolean() {}

            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(0)
        }

        it("does not report when a function's return value is used in a comparison") {
            val code = """
                fun returnsInt() = 42
                
                if (42 == returnsInt()) {}
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(0)
        }
    }
})
