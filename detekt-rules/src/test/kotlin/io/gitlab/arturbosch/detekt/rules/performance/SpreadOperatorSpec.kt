package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.assertj.core.api.Assertions.assertThat

/**
 * @author Ivan Balaksha
 */
class SpreadOperatorSpec : SubjectSpek<SpreadOperator>({
	subject { SpreadOperator() }

	describe("test all possible cases") {
		it("as vararg") {
			val code = """
				fun test0(strs: Array<String>) {
					test(*strs)
				}

				fun test(vararg strs: String) {
					strs.forEach { println(it) }
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("without vararg") {
			val code = """
				fun test0(strs: Array<String>) {
					test(strs)
				}

				fun test(strs: Array<String>) {
					strs.forEach { println(it) }
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("expression inside params") {
			val code = """
				fun test0(strs: Array<String>) {
					test(2*2)
				}

				fun test(test : Int) {
					println(test)
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})