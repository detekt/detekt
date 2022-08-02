package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat as doAssert

private const val MAX_LINE_LENGTH = "maxLineLength"
private const val EXCLUDE_PACKAGE_STATEMENTS = "excludePackageStatements"
private const val EXCLUDE_IMPORT_STATEMENTS = "excludeImportStatements"
private const val EXCLUDE_COMMENT_STATEMENTS = "excludeCommentStatements"

class MaxLineLengthSpec {

    @Nested
    inner class `a kt file with some long lines` {
        private val file = compileForTest(Case.MaxLineLength.path())
        private val lines = file.text.splitToSequence("\n")
        private val fileContent = KtFileContent(file, lines)

        @Test
        fun `should report no errors when maxLineLength is set to 200`() {
            val rule = MaxLineLength(TestConfig(mapOf(MAX_LINE_LENGTH to "200")))

            rule.visit(fileContent)
            assertThat(rule.findings).isEmpty()
        }

        @Test
        fun `should report all errors with default maxLineLength`() {
            val rule = MaxLineLength()

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(3)
        }

        @Test
        fun `should report all errors with default maxLineLength including raw strings`() {
            val rule = MaxLineLength(TestConfig("excludeRawStrings" to false))

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(7)
        }

        @Test
        fun `should report meaningful signature for all violations`() {
            val rule = MaxLineLength()

            rule.visit(fileContent)
            val locations = rule.findings.map { it.signature.substringAfterLast('$') }
            doAssert(locations).allSatisfy { doAssert(it).isNotBlank() }
        }
    }

    @Nested
    inner class `a kt file with long but suppressed lines` {
        private val file = compileForTest(Case.MaxLineLengthSuppressed.path())
        private val lines = file.text.splitToSequence("\n")
        private val fileContent = KtFileContent(file, lines)

        @Test
        fun `should not report as lines are suppressed`() {
            val rule = MaxLineLength()

            rule.visit(fileContent)
            assertThat(rule.findings).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with a long package name and long import statements` {
        val code = """
            package anIncrediblyLongAndComplexPackageNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

            import anIncrediblyLongAndComplexImportNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

            class Test {
            }
        """

        private val file = compileContentForTest(code)
        private val lines = file.text.splitToSequence("\n")
        private val fileContent = KtFileContent(file, lines)

        @Test
        fun `should not report the package statement and import statements by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    mapOf(
                        MAX_LINE_LENGTH to "60"
                    )
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).isEmpty()
        }

        @Test
        fun `should report the package statement and import statements if they're enabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    mapOf(
                        MAX_LINE_LENGTH to "60",
                        EXCLUDE_PACKAGE_STATEMENTS to "false",
                        EXCLUDE_IMPORT_STATEMENTS to "false"
                    )
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(2)
        }

        @Test
        fun `should not report anything if both package and import statements are disabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_PACKAGE_STATEMENTS to "true",
                    EXCLUDE_IMPORT_STATEMENTS to "true",
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with a long package name, long import statements, a long line and long comments` {
        private val file = compileForTest(Case.MaxLineLengthWithLongComments.path())
        private val lines = file.text.splitToSequence("\n")
        private val fileContent = KtFileContent(file, lines)

        @Test
        fun `should report the package statement, import statements, line and comments by default`() {
            val rule = MaxLineLength(
                TestConfig(MAX_LINE_LENGTH to "60")
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(8)
        }

        @Test
        fun `should report the package statement, import statements, line and comments if they're enabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_PACKAGE_STATEMENTS to "false",
                    EXCLUDE_IMPORT_STATEMENTS to "false",
                    EXCLUDE_COMMENT_STATEMENTS to "false",
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(8)
        }

        @Test
        fun `should not report comments if they're disabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    mapOf(
                        MAX_LINE_LENGTH to "60",
                        EXCLUDE_COMMENT_STATEMENTS to "true"
                    )
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(5)
        }
    }

    @Nested
    inner class `a kt file with a long package name, long import statements and a long line` {
        val code = """
            package anIncrediblyLongAndComplexPackageNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

            import anIncrediblyLongAndComplexImportNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot

            class Test {
                fun anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot() {}
            }
        """.trimIndent()

        private val file = compileContentForTest(code)
        private val lines = file.text.splitToSequence("\n")
        private val fileContent = KtFileContent(file, lines)

        @Test
        fun `should only the function line by default`() {
            val rule = MaxLineLength(
                TestConfig(MAX_LINE_LENGTH to "60")
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(1)
        }

        @Test
        fun `should report the package statement, import statements and line if they're not excluded`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_PACKAGE_STATEMENTS to "false",
                    EXCLUDE_IMPORT_STATEMENTS to "false",
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(3)
        }

        @Test
        fun `should report only method if both package and import statements are disabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_PACKAGE_STATEMENTS to "true",
                    EXCLUDE_IMPORT_STATEMENTS to "true",
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(1)
        }

        @Test
        fun `should report correct line and column for function with excessive length`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_PACKAGE_STATEMENTS to "true",
                    EXCLUDE_IMPORT_STATEMENTS to "true",
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(1)
            assertThat(rule.findings).hasStartSourceLocations(SourceLocation(6, 5))
        }
    }
}
