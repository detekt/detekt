package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NotImplementedDeclarationSpec : Spek({
    val subject by memoized { NotImplementedDeclaration() }

    describe("NotImplementedDeclaration rule") {

        it("reports NotImplementedErrors") {
            val code = """
            fun f() {
                if (1 == 1) throw NotImplementedError()
                throw NotImplementedError()
            }"""
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports TODO method calls") {
            val code = """
            fun f() {
                TODO("not implemented")
                TODO()
            }"""
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("does not report TODO comments") {
            val code = """
            fun f() {
                // TODO
            }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
