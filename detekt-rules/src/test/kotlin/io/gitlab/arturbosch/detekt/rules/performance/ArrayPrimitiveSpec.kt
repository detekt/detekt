package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ArrayPrimitiveSpec : SubjectSpek<ArrayPrimitive>({
	subject { ArrayPrimitive() }

	describe("function parameter") {
		it("is Array<Primitive>") {
			val code = "fun function(array: Array<Int>) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isEqualTo(1)
		}

		it("is not an array") {
			val code = "fun function(i: Int) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}

		it("is a specialized array") {
			val code = "fun function(array: IntArray) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}
	}

	describe("return type") {
		it("is Array<Primitive>") {
			val code = "fun returningFunction(): Array<Int> {}"
			subject.lint(code)
			assertThat(subject.findings.size).isEqualTo(1)
		}

		it("is not an array") {
			val code = "fun returningFunction(): Int {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}

		it("is a specialized array") {
			val code = "fun returningFunction(): IntArray {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}
	}
})
