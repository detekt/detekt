package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ArrayPrimitiveSpec : SubjectSpek<ArrayPrimitive>({
	subject { ArrayPrimitive() }

	describe("one function parameter") {
		it("is an array of primitive type") {
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
			val code = "fun function(array: ByteArray) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}

		it("is not present") {
			val code = "fun function() {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}

		it("is an array of a non-primitive type") {
			val code = "fun function(array: Array<String>) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}

		it("is an array of an array of a primitive type") {
			val code = "fun function(array: Array<Array<Int>>) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isEqualTo(1)
		}

		it("is an array of an array of a non-primitive type") {
			val code = "fun function(array: Array<Array<String>>) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}
	}

	describe("multiple function parameters") {
		it("one is Array<Primitive> and the other is not") {
			val code = "fun function(array: Array<Int>, array2: IntArray) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isEqualTo(1)
		}

		it("both are arrays of primitive types") {
			val code = "fun function(array: Array<Int>, array2: Array<Double>) {}"
			subject.lint(code)
			assertThat(subject.findings.size).isEqualTo(2)
		}
	}

	describe("return type") {
		it("is Array<Primitive>") {
			val code = "fun returningFunction(): Array<Float> {}"
			subject.lint(code)
			assertThat(subject.findings.size).isEqualTo(1)
		}

		it("is not an array") {
			val code = "fun returningFunction(): Int {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}

		it("is a specialized array") {
			val code = "fun returningFunction(): CharArray {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}

		it("is not explicitly set") {
			val code = "fun returningFunction() {}"
			subject.lint(code)
			assertThat(subject.findings.size).isZero()
		}
	}
})
