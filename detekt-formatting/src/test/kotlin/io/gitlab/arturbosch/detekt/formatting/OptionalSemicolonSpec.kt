package io.gitlab.arturbosch.detekt.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.gitlab.arturbosch.detekt.test.format
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class UselessSemicolonSpec : SubjectSpek<OptionalSemicolon>({
	subject { OptionalSemicolon() }

	describe("common semicolon cases") {
		it("finds useless semicolon") {
			val actual = subject.format("""
			// here is a ; semicolon
            fun main() {
                fun name() { a(); return b }
                println(";")
                println();
            }
			""")
			val expected = """
			// here is a ; semicolon
            fun main() {
                fun name() { a(); return b }
                println(";")
                println()
            }
			""".trimIndent()
			assertThat(actual, equalTo(expected))
		}

		it("does not deletes statement separation semicolon") {
			val actual = subject.format("""
            fun main() {
                fun name() { a();return b }
            };
            """)
			val expected = """
            fun main() {
                fun name() { a();return b }
            }
			""".trimIndent()
			assertThat(actual, equalTo(expected))
		}

		it("does not deletes statement separation semicolon (format test)") {
			val actual = subject.format("""
            fun main() {
                fun name() { a();return b }
            };
            """)
			val expected = """
            fun main() {
                fun name() { a();return b }
            }
			""".trimIndent()
			assertThat(actual, equalTo(expected))
		}

		it("should not delete all semicolons") {
			val actual = subject.format("""
            fun main() {
                println();;;;println()
            }
            """)
			val expected = """
            fun main() {
                println();println()
            }
			""".trimIndent()
			assertThat(actual, equalTo(expected))
		}
	}

})
