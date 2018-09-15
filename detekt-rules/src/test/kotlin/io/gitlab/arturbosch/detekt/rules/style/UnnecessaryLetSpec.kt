package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnnecessaryLetSpec : SubjectSpek<UnnecessaryLet>({
	subject { UnnecessaryLet(Config.empty) }

	describe("check lets") {
		it("has unnecessary lets") {
			val findings = subject.lint("""
				fun f() {
					val a : Int? = null
					a?.let { it.plus(1) }
					a.let { that -> that.plus(1) }
				}""")
			assertThat(findings).hasSize(2)
		}
	}
})
