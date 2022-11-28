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
private const val EXCLUDE_RAW_STRINGS = "excludeRawStrings"

class MaxLineLengthSpec {

    @Nested
    inner class `a kt file with some long lines` {
        private val file = compileForTest(Case.MaxLineLength.path())

        @Test
        fun `should report no errors when maxLineLength is set to 200`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "200",
                )
            )

            rule.visitKtFile(file)
            assertThat(rule.findings).isEmpty()
        }

        @Test
        fun `should report all errors with default maxLineLength`() {
            val rule = MaxLineLength()

            rule.visitKtFile(file)
            assertThat(rule.findings).hasSize(3)
        }

        @Test
        fun `should report all errors with default maxLineLength including raw strings`() {
            val rule = MaxLineLength(
                TestConfig(
                    EXCLUDE_RAW_STRINGS to false,
                )
            )

            rule.visitKtFile(file)
            assertThat(rule.findings).hasSize(7)
        }

        @Test
        fun `should report meaningful signature for all violations`() {
            val rule = MaxLineLength()

            rule.visitKtFile(file)
            val locations = rule.findings.map { it.signature.substringAfterLast('$') }
            doAssert(locations).allSatisfy { doAssert(it).isNotBlank() }
        }
    }

    @Nested
    inner class `a kt file with long but suppressed lines` {
        private val file = compileForTest(Case.MaxLineLengthSuppressed.path())

        @Test
        fun `should not report as lines are suppressed`() {
            val rule = MaxLineLength()

            rule.visitKtFile(file)
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
        """.trimIndent()

        private val file = compileContentForTest(code)

        @Test
        fun `should not report the package statement and import statements by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            rule.visitKtFile(file)
            assertThat(rule.findings).isEmpty()
        }

        @Test
        fun `should report the package statement and import statements if they're enabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_PACKAGE_STATEMENTS to "false",
                    EXCLUDE_IMPORT_STATEMENTS to "false",
                )
            )

            rule.visitKtFile(file)
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

            rule.visitKtFile(file)
            assertThat(rule.findings).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with a long package name, long import statements, a long line and long comments` {
        private val file = compileForTest(Case.MaxLineLengthWithLongComments.path())

        @Test
        fun `should report the package statement, import statements, line and comments by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            rule.visitKtFile(file)
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

            rule.visitKtFile(file)
            assertThat(rule.findings).hasSize(8)
        }

        @Test
        fun `should not report comments if they're disabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_COMMENT_STATEMENTS to "true",
                )
            )

            rule.visitKtFile(file)
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

        @Test
        fun `should only the function line by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            rule.visitKtFile(file)
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

            rule.visitKtFile(file)
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

            rule.visitKtFile(file)
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

            rule.visitKtFile(file)
            assertThat(rule.findings).hasSize(1)
            assertThat(rule.findings)
                .hasStartSourceLocations(SourceLocation(6, 1))
                .hasEndSourceLocation(6, 109)
        }
    }

    @Test
    fun `report the correct lines on raw strings with backslash on it - issue #5314`() {
        val rule = MaxLineLength(
            TestConfig(
                MAX_LINE_LENGTH to "30",
                "excludeRawStrings" to "false",
            )
        )

        rule.visitKtFile(
            compileContentForTest(
                """
                    // some other content
                    val x = Regex(${"\"\"\""}
                        Text (.*?)\(in parens\) this is too long to be valid.
                        The regex/raw string continues down another line      .
                    ${"\"\"\""}.trimIndent())
                    // that is the right length
                """.trimIndent()
            )
        )
        assertThat(rule.findings).hasSize(2)
        assertThat(rule.findings).hasTextLocations(40 to 97, 98 to 157)
    }
}
