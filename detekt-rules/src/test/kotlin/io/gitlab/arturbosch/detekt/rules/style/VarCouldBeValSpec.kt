package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class VarCouldBeValSpec : SubjectSpek<VarCouldBeVal>({

	subject { VarCouldBeVal() }

	given("local declarations in functions") {

		it("does not report variables that are re-assigned") {
			val code = """
    		fun test() {
				var a = 1
				a = 2
			}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report variables that are re-assigned with assignment operator") {
			val code = """
			fun test() {
				var a = 1
				a += 2
			}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report variables that are re-assigned with assignment operator") {
			val code = """
    		fun test() {
				var a = 1
				a += 2
			}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report variables that are re-assigned with postfix operators") {
			val code = """
			fun test() {
				var a = 1
				a++
			}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report variables that are re-assigned with infix operators") {
			val code = """
			fun test() {
				var a = 1
				--a
			}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report variables that are re-assigned inside scope functions") {
			val code = """
			fun test() {
				var a = 1
				a.also {
					a = 2
				}
			}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("reports variables that are not re-assigned, but used in expressions") {
			val code = """
			fun test() {
				var a = 1
				val b = a + 2
			}
			"""
			val lint = subject.lint(code)

			assertThat(lint).hasSize(1)
			with(lint[0]) {
				assertThat(entity.name).isEqualTo("a")
			}
		}

		it("reports variables that are not re-assigned, but used in function calls") {
			val code = """
			fun test() {
				var a = 1
				something(a)
			}
			"""
			val lint = subject.lint(code)

			assertThat(lint).hasSize(1)
			with(lint[0]) {
				assertThat(entity.name).isEqualTo("a")
			}
		}

		it("reports variables that are not re-assigned, but shadowed by one that is") {
			val code = """
			fun test() {
				var shadowed = 1
				fun nestedFunction() {
					var shadowed = 2
					shadowed = 3
				}
			}
			"""
			val lint = subject.lint(code)

			assertThat(lint).hasSize(1)
			with(lint[0].entity) {
				assertThat(ktElement?.text).isEqualTo("var shadowed = 1")
			}
		}
	}
})
