package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CommentOverPrivatePropertiesSpec : Spek({
    val subject by memoized { CommentOverPrivateProperty() }

    describe("CommentOverPrivateProperty rule") {

        it("reports private property with a comment") {
            val code = """
                /**
                 * asdf
                 */
                private val v = 1"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report public property with a comment") {
            val code = """
                /**
                 * asdf
                 */
                val v = 1"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports private property in class with a comment") {
            val code = """
                    class Test {
                    /**
                     * asdf
                     */
                    private val v = 1
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report public property with a comment") {
            val code = """
                class Test {
                    /**
                     * asdf
                     */
                    val v = 1
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
