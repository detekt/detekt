package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SafeCastSpec : Spek({
    val subject by memoized { SafeCast() }

    describe("SafeCast rule") {

        it("reports negated expression") {
            val code = """
                fun test(element: Int) {
                    val cast = if (element !is Number) {
                        null
                    } else {
                        element
                    }
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports expression") {
            val code = """
                fun test(element: Int) {
                    val cast = if (element is Number) {
                        element
                    } else {
                        null
                    }
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report wrong condition") {
            val code = """
                fun test(element: Int) {
                    val other = 3
                    val cast = if (element == other) {
                        element
                    } else {
                        null
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report wrong else clause") {
            val code = """
                fun test(element: Int) {
                    val cast = if (element is Number) {
                        element
                    } else {
                        String()
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
