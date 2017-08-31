package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Ivan Balaksha
 */

class DataClassContainsFunctionsSpec : SubjectSpek<DataClassContainsFunctionsRule>({
	subject { DataClassContainsFunctionsRule() }

	describe("") {

		it("valid data class") {
			val code = """data class Test(val id : String)"""
			Assertions.assertThat(subject.lint(code)).hasSize(0)
		}

		it("data class contains printTest function") {
			val code = """
				data class Test(val test : String){
                    fun printTest(){
                        println(test)
                    }
                }"""
			Assertions.assertThat(subject.lint(code)).hasSize(1)
		}

		it("data class contains two function") {
			val code = """
				data class Test(val test : String){
                    fun printTest(){
                        println(test)
                    }
                    fun printTestTwice(){
                        println(test)
                        println(test)
                    }
                }"""
			Assertions.assertThat(subject.lint(code)).hasSize(2)
		}
		it("data class use overridden equals") {
			val code = """
				data class Test(val test : String){
                    override fun equals(other: Any?): Boolean {
                        return super.equals(other)
                    }
                }"""
			Assertions.assertThat(subject.lint(code)).hasSize(0)
		}

		it("data class use overridden hashcode") {
			val code = """
				data class Test(val test : String){
                    override fun hashCode(): Int {
                        return super.hashCode()
                    }
                }"""
			Assertions.assertThat(subject.lint(code)).hasSize(0)
		}

		it("data class use overridden toString") {
			val code = """
				data class Test(val test : String){
                    override fun toString(): String {
                        return super.toString()
                    }
                }"""
			Assertions.assertThat(subject.lint(code)).hasSize(0)
		}
	}
})