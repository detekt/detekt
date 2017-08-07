package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertEquals

class UnnecessaryTemporaryInstantiationSpec : SubjectSpek<UnnecessaryTemporaryInstantiation>({
	subject { UnnecessaryTemporaryInstantiation() }

	describe("temporary instantiation for conversion") {
		val code = "val i = Integer(1).toString()"
		subject.lint(code)
		assertEquals(subject.findings.size, 1)
	}

	describe("right conversion without instantiation") {
		val code = "val i = Integer.toString(1)"
		subject.lint(code)
		assertEquals(subject.findings.size, 0)
	}
})
