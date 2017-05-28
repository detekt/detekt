package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.test.format
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class OptionalSemicolonSpec : SubjectSpek<OptionalSemicolon>({
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
			assertThat(actual).isEqualTo(expected)
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
			assertThat(actual).isEqualTo(expected)
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
			assertThat(actual).isEqualTo(expected)
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
			assertThat(actual).isEqualTo(expected)
		}

		it("does not delete :: in class references") {
			val actual = subject.format("""
            fun main() {
                val s = String::class
            }
            """)
			val expected = """
            fun main() {
                val s = String::class
            }
			""".trimIndent()
			assertThat(actual).isEqualTo(expected)

		}
	}

})
