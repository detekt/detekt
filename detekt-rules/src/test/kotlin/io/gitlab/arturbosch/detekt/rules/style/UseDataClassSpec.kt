package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Ivan Balaksha
 */
class UseDataClassSpec : SubjectSpek<UseDataClass>({
	subject { UseDataClass(Config.empty) }

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
	it("should not report issue for object"){
		val code = """
			object Test{
				val test = "Test"
				val test2 = "Test2"
			}
		"""
		Assertions.assertThat(subject.lint(code)).hasSize(0)
	}
	it("should not report issue for data class with overridden toString"){
		val code = """
			data class Test(val test : String){
				override fun toString(): String {
					return "TEST"
				}
			}
		"""
		Assertions.assertThat(subject.lint(code)).hasSize(0)
	}
	it("should report issue for class with all accepted functions"){
		val code = """
			class Test(val test : String){
				override fun equals(other: Any?): Boolean {
					return super.equals(other)
				}

				override fun hashCode(): Int {
					return super.hashCode()
				}

				override fun toString(): String {
					return super.toString()
				}
			}

		"""
		Assertions.assertThat(subject.lint(code)).hasSize(1)
	}
	it("should report issue for class with all accepted functions and one additional"){
		val code = """
			class Test(val test : String){
				override fun equals(other: Any?): Boolean {
					return super.equals(other)
				}

				override fun hashCode(): Int {
					return super.hashCode()
				}

				override fun toString(): String {
					return super.toString()
				}
				fun print(){
					println(test)
				}
			}

		"""
		Assertions.assertThat(subject.lint(code)).hasSize(0)
	}
})

