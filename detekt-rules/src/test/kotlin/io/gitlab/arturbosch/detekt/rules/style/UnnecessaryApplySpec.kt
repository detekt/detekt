package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnnecessaryApplySpec : SubjectSpek<UnnecessaryApply>({

	subject { UnnecessaryApply(Config.empty) }

	given("unnecessary applies that can be changed to ordinary method call") {

		it("reports an apply on non-nullable type") {
			assertThat(subject.lint("""
				fun f() {
					val a : Int? = null
					a.apply {
						plus(1)
					}
				}
			""")).hasSize(1)
		}

		it("reports an apply on nullable type") {
			assertThat(subject.lint("""
				fun f() {
					val a : Int? = null
					a?.apply {
						plus(1)
					}
				}
			""")).hasSize(1)
		}

		it("does not report an apply with lambda block") {
			assertThat(subject.lint("""
				fun f() {
					val a : Int? = null
					a.apply({
						plus(1)
					})
				}
			""")).isEmpty()
		}

		it("does report single statement in apply used as function argument") {
			assertThat(subject.lint("""
				fun b(i : Int?) {
				}
				fun f() {
					val a : Int? = null
					b(a.apply {
						toString()
					})
				}
			""")).hasSize(1)
		}

		it("does not report applies with lambda body containing more than one statement") {
			val findings = subject.lint("""
				fun b(i : Int?) {
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
			assertThat(findings).isEmpty()
		}
	}
})
