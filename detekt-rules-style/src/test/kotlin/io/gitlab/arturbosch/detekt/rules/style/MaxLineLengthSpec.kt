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

        val file = compileForTest(Case.MaxLineLength.path())
        val lines = file.text.splitToSequence("\n")
        val fileContent = KtFileContent(file, lines)

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

        val file = compileForTest(Case.MaxLineLengthSuppressed.path())
        val lines = file.text.splitToSequence("\n")
        val fileContent = KtFileContent(file, lines)

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

        val file = compileContentForTest(code)
        val lines = file.text.splitToSequence("\n")
        val fileContent = KtFileContent(file, lines)

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
                    mapOf(
                        MAX_LINE_LENGTH to "60",
                        EXCLUDE_PACKAGE_STATEMENTS to "true",
                        EXCLUDE_IMPORT_STATEMENTS to "true"
                    )
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with a long package name, long import statements, a long line and long comments` {

        val file = compileForTest(Case.MaxLineLengthWithLongComments.path())
        val lines = file.text.splitToSequence("\n")
        val fileContent = KtFileContent(file, lines)

        @Test
        fun `should report the package statement, import statements, line and comments by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    mapOf(
                        MAX_LINE_LENGTH to "60"
                    )
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(8)
        }

        @Test
        fun `should report the package statement, import statements, line and comments if they're enabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    mapOf(
                        MAX_LINE_LENGTH to "60",
                        EXCLUDE_PACKAGE_STATEMENTS to "false",
                        EXCLUDE_IMPORT_STATEMENTS to "false",
                        EXCLUDE_COMMENT_STATEMENTS to "false"
                    )
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

        val file = compileContentForTest(code)
        val lines = file.text.splitToSequence("\n")
        val fileContent = KtFileContent(file, lines)

        @Test
        fun `should only the function line by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    mapOf(
                        MAX_LINE_LENGTH to "60"
                    )
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(1)
        }

        @Test
        fun `should report the package statement, import statements and line if they're not excluded`() {
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
            assertThat(rule.findings).hasSize(3)
        }

        @Test
        fun `should report only method if both package and import statements are disabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    mapOf(
                        MAX_LINE_LENGTH to "60",
                        EXCLUDE_PACKAGE_STATEMENTS to "true",
                        EXCLUDE_IMPORT_STATEMENTS to "true"
                    )
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(1)
        }

        @Test
        fun `should report correct line and column for function with excessive length`() {
            val rule = MaxLineLength(
                TestConfig(
                    mapOf(
                        MAX_LINE_LENGTH to "60",
                        EXCLUDE_PACKAGE_STATEMENTS to "true",
                        EXCLUDE_IMPORT_STATEMENTS to "true"
                    )
                )
            )

            rule.visit(fileContent)
            assertThat(rule.findings).hasSize(1)
            assertThat(rule.findings).hasSourceLocations(SourceLocation(6, 5))
        }
    }

    @Nested
    inner class `a kt file with raw string with max line length` {
        val code = """
            class Test {
             private fun defaultConfigConfiguration(): String = ""${'"'}
          config:
          validation: true
          warningsAsErrors: false
          # when writing own rules with new properties, exclude the property path e.g.: 'my_rule_set,.*>.*>[my_property]' perties, exclude the property path e.g.: 'my_rule_set,.*>.*>[my_property]'
          excludes: ''
    ""${'"'}.trimIndent()
        }
        """.trimIndent()
        val file = compileContentForTest(code)
        val lines = file.text.splitToSequence("\n")
        val fileContent = KtFileContent(file, lines)

        @Test
        fun `should not report max line length in raw string`() {
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
    }

    @Nested
    inner class `a kt file with raw string with max line and leading quotes` {
        val code = """
            class Test {
             private fun longMultiLineFieldWithLeadingQuote(): String = ""${'"'}
            "This is yet another very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very"
            "very long multiline String with Line Break that will break the MaxLineLength"
    ""${'"'}.trimIndent()
            }
        """.trimIndent()
        val file = compileContentForTest(code)
        val lines = file.text.splitToSequence("\n")
        val fileContent = KtFileContent(file, lines)

        @Test
        fun `should not report max line length in raw string`() {
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
    }
}
