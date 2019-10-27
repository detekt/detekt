package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CommentOverPrivateMethodSpec : Spek({
    val subject by memoized { CommentOverPrivateFunction() }

    describe("CommentOverPrivateFunction rule") {

        it("reports private method with a comment") {
            val code = """
                class Test {
                    /**
                     * asdf
                     */
                    private fun f() {}
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report public method with a comment") {
            val code = """
                /**
                 * asdf
                 */
                fun f() {}"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report public method in a class with a comment") {
            val code = """
                class Test {
                    /**
                     * asdf
                     */
                    fun f() {}
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
