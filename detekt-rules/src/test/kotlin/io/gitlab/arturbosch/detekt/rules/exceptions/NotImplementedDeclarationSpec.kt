package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NotImplementedDeclarationSpec : SubjectSpek<NotImplementedDeclaration>({
    subject { NotImplementedDeclaration() }

    given("throwing NotImplementedError declarations") {

        it("reports NotImplementedErrors") {
            val code = """
				fun f() {
					if (1 == 1) throw NotImplementedError()
					throw NotImplementedError()
				}"""
            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    given("several TODOs") {

        it("reports TODO method calls") {
            val code = """
				fun f() {
					TODO("not implemented")
					TODO()
				}"""
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("does not report TODO comments") {
            val code = """
				fun f() {
					// TODO
				}"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }
})
