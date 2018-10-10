package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnnecessaryApplySpec : SubjectSpek<UnnecessaryApply>({
	subject { UnnecessaryApply(Config.empty) }

	given("some code using apply expressions extensively") {
		it("reports unnecessary applies that can be changed to ordinary method call") {
			val findings = subject.lint("""
				fun b(i : Int) {
				}
				fun f() {
					val a : Int? = null
					a.apply {
						plus(1)
					}
					a?.apply {
						plus(1)
					}
					a.apply({
						plus(1)
					})
					b(a.apply {
						plus(1)
					})
				}""")
			assertThat(findings).hasSize(3)
		}
		it("does not report applies with lambda body containing more than one statement") {
			val findings = subject.lint("""
				fun b(i : Int) {
				}
				fun f() {
					val a : Int? = null
					a.apply {
						plus(1)
						plus(2)
					}
					a?.apply {
						plus(1)
						plus(2)
					}
					b(1.apply {
						plus(1)
						plus(2)
					})
				}""")
			assertThat(findings).hasSize(0)
		}
	}
})
