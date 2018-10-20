package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.subject.SubjectSpek

class ArrayPrimitiveSpec : SubjectSpek<ArrayPrimitive>({
	subject { ArrayPrimitive() }

	describe("function with Array<Primitive> parameter") {
		val code = "fun function(array: Array<Int>) {}"
		subject.lint(code)
		assertThat(subject.findings.size).isEqualTo(1)
	}

	describe("function with non-array parameter") {
		val code = "fun function(i: Int) {}"
		subject.lint(code)
		assertThat(subject.findings.size).isZero()
	}

	describe("function with PrimitiveArray") {
		val code = "fun function(array: IntArray) {}"
		subject.lint(code)
		assertThat(subject.findings.size).isZero()
	}
})
