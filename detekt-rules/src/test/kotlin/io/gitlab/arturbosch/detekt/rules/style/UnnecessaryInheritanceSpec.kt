package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnnecessaryInheritanceSpec : SubjectSpek<UnnecessaryInheritance>({
	subject { UnnecessaryInheritance(Config.empty) }

	describe("check inherit classes") {

		it("has unnecessary super type declarations") {
			val findings = subject.lint("""
				class A : Any()
				class B : Object()""")
			assertThat(findings).hasSize(2)
		}

		it("has no unnecessary super type declarations") {
			val findings = subject.lint("class C : An()")
			assertThat(findings).hasSize(0)
		}
	}
})
