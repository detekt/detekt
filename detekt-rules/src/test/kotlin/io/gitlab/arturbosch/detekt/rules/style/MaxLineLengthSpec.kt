package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MaxLineLengthSpec : Spek({

    describe("MaxLineLength rule") {

        context("a kt file with some long lines") {
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
                assertThat(rule.findings).hasSize(6)
            }
        }

        context("a kt file with long but suppressed lines") {
            val file = compileForTest(Case.MaxLineLengthSuppressed.path())
            val lines = file.text.splitToSequence("\n")
            val fileContent = KtFileContent(file, lines)

            it("should not report as lines are suppressed") {
                val rule = MaxLineLength()

                rule.visit(fileContent)
                assertThat(rule.findings).isEmpty()
            }
        }

        context("a kt file with a long package name and long import statements") {
            val code = """
			package anIncrediblyLongAndComplexPackageNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

			import anIncrediblyLongAndComplexImportNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

			class Test {
			}
		"""

            val file = compileContentForTest(code)
            val lines = file.text.splitToSequence("\n")
            val fileContent = KtFileContent(file, lines)

            it("should not report the package statement and import statements by default") {
                val rule = MaxLineLength(TestConfig(mapOf(
                        "maxLineLength" to "60"
                )))

                rule.visit(fileContent)
                assertThat(rule.findings).isEmpty()
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

        context("a kt file with a long package name, long import statements, a long line and long comments") {
            val file = compileForTest(Case.MaxLineLengthWithLongComments.path())
            val lines = file.text.splitToSequence("\n")
            val fileContent = KtFileContent(file, lines)

            it("should report the package statement, import statements, line and comments by default") {
                val rule = MaxLineLength(TestConfig(mapOf(
                        "maxLineLength" to "60"
                )))

                rule.visit(fileContent)
                assertThat(rule.findings).hasSize(8)
            }

            it("should report the package statement, import statements, line and comments if they're enabled") {
                val rule = MaxLineLength(TestConfig(mapOf(
                        "maxLineLength" to "60",
                        "excludePackageStatements" to "false",
                        "excludeImportStatements" to "false",
                        "excludeCommentStatements" to "false"
                )))

                rule.visit(fileContent)
                assertThat(rule.findings).hasSize(8)
            }

            it("should not report comments if they're disabled") {
                val rule = MaxLineLength(TestConfig(mapOf(
                        "maxLineLength" to "60",
                        "excludeCommentStatements" to "true"
                )))

                rule.visit(fileContent)
                assertThat(rule.findings).hasSize(5)
            }
        }

        context("a kt file with a long package name, long import statements and a long line") {
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

            it("should only the function line by default") {
                val rule = MaxLineLength(TestConfig(mapOf(
                        "maxLineLength" to "60"
                )))

                rule.visit(fileContent)
                assertThat(rule.findings).hasSize(1)
            }

            it("should report the package statement, import statements and line if they're not excluded") {
                val rule = MaxLineLength(TestConfig(mapOf(
                        "maxLineLength" to "60",
                        "excludePackageStatements" to "false",
                        "excludeImportStatements" to "false"
                )))

                rule.visit(fileContent)
                assertThat(rule.findings).hasSize(3)
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

            it("should report correct line and column for function with excessive length") {
                val rule = MaxLineLength(TestConfig(mapOf(
                        "maxLineLength" to "60",
                        "excludePackageStatements" to "true",
                        "excludeImportStatements" to "true"
                )))

                rule.visit(fileContent)
                assertThat(rule.findings).hasSize(1)
                val findingSource = rule.findings[0].location.source
                assertThat(findingSource.line).isEqualTo(6)
                assertThat(findingSource.column).isEqualTo(5)
            }
        }
    }
})
