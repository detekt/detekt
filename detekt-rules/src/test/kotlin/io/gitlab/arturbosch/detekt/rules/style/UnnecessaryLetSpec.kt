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
					val b = a.let { it.plus(3) }.let { a -> a.minus(1) }
					val c = b
					b.let { it?.plus(c) }
					a?.let { it.plus(b) }
				}""")
			assertThat(findings).hasSize(4)
		}
	}
})
