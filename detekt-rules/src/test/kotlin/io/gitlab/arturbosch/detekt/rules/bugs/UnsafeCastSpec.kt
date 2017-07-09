package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.assertj.core.api.Assertions.assertThat

/**
 * @author Ivan Balaksha
 */
class UnsafeCastSpec : SubjectSpek<UnsafeCast>({
	subject { UnsafeCast() }

	describe("check safe and unsafe casts") {

		it("test unsafe cast") {
			val code = """
				fun test(s: Any) {
					println(s as Int)
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("test safe cast") {
			val code = """
				fun test(s: Any) {
					println((s as? Int) ?: 0)
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}
})