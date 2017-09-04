package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnnecessaryAbstractClassSpec : SubjectSpek<UnnecessaryAbstractClass>({
	subject { UnnecessaryAbstractClass(Config.empty) }

	given("abstract classes with some members") {

		it("has unnecessary properties and functions") {
			val file = compileForTest(Case.UnnecessaryAbstractClass.path())
			assertThat(subject.lint(file.text).size).isEqualTo(4)
		}
	}
})
