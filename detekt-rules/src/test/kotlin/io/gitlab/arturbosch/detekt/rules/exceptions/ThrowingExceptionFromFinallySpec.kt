package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ThrowingExceptionFromFinallySpec : Spek({
    val subject by memoized { ThrowingExceptionFromFinally() }

    describe("ThrowingExceptionFromFinally rule") {

        it("should report a throw expression") {
            val code = """
                fun x() {
                    try {
                    } finally {
                        if (1 == 1) {
                            throw IllegalArgumentException()
                        }
                    }
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should report a nested throw expression") {
            val code = """
                fun x() {
                    try {
                    } finally {
                        throw IllegalArgumentException()
                    }
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should not report a finally expression without a throw expression") {
            val code = """
                fun x() {
                    try {
                    } finally {
                        println()
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
