package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.subject.SubjectSpek

class UnnecessaryTemporaryInstantiationSpec : SubjectSpek<UnnecessaryTemporaryInstantiation>({
	subject { UnnecessaryTemporaryInstantiation() }

	describe("temporary instantiation for conversion") {
		val code = "val i = Integer(1).toString()"
		subject.lint(code)
		assertThat(subject.findings.size).isEqualTo(1)
	}

	describe("right conversion without instantiation") {
		val code = "val i = Integer.toString(1)"
		subject.lint(code)
		assertThat(subject.findings.size).isZero()
	}
})
