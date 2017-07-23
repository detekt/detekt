package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.verify
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class MaxLineLengthRuleSpec : Spek({

	given("a kt file with some long lines") {
		val file = compileForTest(Case.MaxLineLength.path())
		val lines = file.text.splitToSequence("\n")
		val fileContent = KtFileContent(file, lines)

		it("should report no errors when maxLineLength is set to 200") {
			val rule = MaxLineLength(TestConfig(mapOf("maxLineLength" to "200")))

			rule.verify(fileContent) {
				Assertions.assertThat(it).isEmpty()
			}
		}

		it("should report all errors with default maxLineLength") {
			val rule = MaxLineLength()

			rule.verify(fileContent) {
				Assertions.assertThat(it).hasSize(3)
			}
		}
	}

	given("a kt file a long package name and long import statements") {
		val code = """
			package veeeeeeeeeeeeeerylong.statement.that.is.longer.than.onehundredtwenty.characters.which.detekt.should.report.by.default

			import veeeeeeeeeeeeeerylong.statement.that.is.longer.than.onehundredtwenty.characters.which.detekt.should.report.by.default

			class Test {
			}
		"""

		val file = compileContentForTest(code)
		val lines = file.text.splitToSequence("\n")
		val fileContent = KtFileContent(file, lines)

		it("should report the package statement and import statements by default") {
			val rule = MaxLineLength()

			rule.verify(fileContent) {
				Assertions.assertThat(it).hasSize(2)
			}
		}

		it("should report the package statement and import statements if they're enabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"excludePackageStatements" to "false",
					"excludeImportStatements" to "false"
			)))

			rule.verify(fileContent) {
				Assertions.assertThat(it).hasSize(2)
			}
		}

		it("should not report the package statement if it is disabled") {
			val rule = MaxLineLength(TestConfig(mapOf("excludePackageStatements" to "true")))

			rule.verify(fileContent) {
				Assertions.assertThat(it).hasSize(1)
			}
		}

		it("should not report the import statements if it is disabled") {
			val rule = MaxLineLength(TestConfig(mapOf("excludeImportStatements" to "true")))

			rule.verify(fileContent) {
				Assertions.assertThat(it).hasSize(1)
			}
		}

		it("should not report anything if both package and import statements are disabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"excludePackageStatements" to "true",
					"excludeImportStatements" to "true"
			)))

			rule.verify(fileContent) {
				Assertions.assertThat(it).isEmpty()
			}
		}
	}
})
