package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Ivan Balaksha
 */
class UseDataClassSpec : SubjectSpek<UseDataClassRule>({
	subject { UseDataClassRule(Config.empty) }

	it("should report error for class w/ constructor properties and w/o functions") {
		val code = """
			class Test(val test : String){ }
		"""
		Assertions.assertThat(subject.lint(code)).hasSize(1)
	}
	it("should not report error for class w/ constructor properties and w/ functions") {
		val code = """
			class Test(val test : String){
				fun test(){
					println(test)
				}
			}
		"""
		Assertions.assertThat(subject.lint(code)).hasSize(0)
	}
	it("should report error for class w/ properties and w/o functions") {
		val code = """
			class Test(temp: String) {
				val test = temp
			}
		"""
		Assertions.assertThat(subject.lint(code)).hasSize(1)
	}
})

