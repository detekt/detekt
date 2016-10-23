package io.gitlab.arturbosch.detekt.rules

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.compileContentForTest
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.SubjectDsl
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class UselessSemicolonSpec : SubjectSpek<OptionalSemicolon>({
	subject { OptionalSemicolon() }

	describe("common semicolon cases") {
		it("finds useless semicolon") {
			val code = """
			// here is a ; semicolon
            fun main() {
                fun name() { a(); return b }
                println(";")
                println();
            }
			"""
			execute(code)
			assertThat(subject.findings, hasSize(equalTo(1)))
		}

		it("does not deletes statement separation semicolon") {
			val code = """
            fun main() {
                fun name() { a();return b }
            };
            """
			execute(code)
			assertThat(subject.findings, hasSize(equalTo(1)))
		}

		it("should find two double semicolons") {
			val code = """
            fun main() {
                println();;;;println()
            }
            """
			execute(code)
			assertThat(subject.findings, hasSize(equalTo(2)))
		}
	}

})

private fun SubjectDsl<OptionalSemicolon>.execute(code: String) {
	val root = compileContentForTest(code)
	subject.visit(root)
}