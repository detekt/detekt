package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class MaxLineLengthSpec : Spek({

	given("a kt file with some long lines") {
		val file = compileForTest(Case.MaxLineLength.path())
		val lines = file.text.splitToSequence("\n")
		val fileContent = KtFileContent(file, lines)

		it("should report no errors when maxLineLength is set to 200") {
			val rule = MaxLineLength(TestConfig(mapOf("maxLineLength" to "200")))

			rule.visit(fileContent)
			assertThat(rule.findings).isEmpty()
		}

		it("should report all errors with default maxLineLength") {
			val rule = MaxLineLength()

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(3)
		}
	}

	given("a kt file with a long package name and long import statements") {
		val code = """
			package anIncrediblyLongAndComplexPackageNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

			import anIncrediblyLongAndComplexImportNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

			class Test {
			}
		"""

		val file = compileContentForTest(code)
		val lines = file.text.splitToSequence("\n")
		val fileContent = KtFileContent(file, lines)

		it("should report the package statement and import statements by default") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(2)
		}

		it("should report the package statement and import statements if they're enabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludePackageStatements" to "false",
					"excludeImportStatements" to "false"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(2)
		}

		it("should not report the package statement if it is disabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludePackageStatements" to "true"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(1)
		}

		it("should not report the import statements if it is disabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludeImportStatements" to "true"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(1)
		}

		it("should not report anything if both package and import statements are disabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludePackageStatements" to "true",
					"excludeImportStatements" to "true"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).isEmpty()
		}
	}

	given("a kt file with a long package name, long import statements and a long line") {
		val code = """
			package anIncrediblyLongAndComplexPackageNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

			import anIncrediblyLongAndComplexImportNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

			class Test {
				fun anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot() {}
			}
		""".trim()

		val file = compileContentForTest(code)
		val lines = file.text.splitToSequence("\n")
		val fileContent = KtFileContent(file, lines)

		it("should report the package statement, import statements and line by default") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(3)
		}

		it("should report the package statement, import statements and line if they're enabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludePackageStatements" to "false",
					"excludeImportStatements" to "false"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(3)
		}

		it("should not report the package statement if it is disabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludePackageStatements" to "true"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(2)
		}

		it("should not report the import statements if it is disabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludeImportStatements" to "true"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(2)
		}

		it("should report only method if both package and import statements are disabled") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludePackageStatements" to "true",
					"excludeImportStatements" to "true"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(1)
		}

		it("should report correct line and column for the finding") {
			val rule = MaxLineLength(TestConfig(mapOf(
					"maxLineLength" to "60",
					"excludePackageStatements" to "true",
					"excludeImportStatements" to "true"
			)))

			rule.visit(fileContent)
			assertThat(rule.findings).hasSize(1)
			val findingSource = rule.findings[0].location.source
			assertThat(findingSource.line).isEqualTo(6)
			assertThat(findingSource.column).isEqualTo(109)
		}
	}
})
