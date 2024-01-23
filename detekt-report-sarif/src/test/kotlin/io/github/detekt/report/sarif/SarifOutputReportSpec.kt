package io.github.detekt.report.sarif

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.test.EmptySetupContext
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createFindingForRelativePath
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

class SarifOutputReportSpec {

    @Test
    fun `renders multiple issues`() {
        val result = TestDetektion(
            createFinding(ruleName = "TestSmellA", severity = Severity.Error),
            createFinding(ruleName = "TestSmellB", severity = Severity.Warning),
            createFinding(ruleName = "TestSmellC", severity = Severity.Info)
        )

        val report = SarifOutputReport()
            .apply { init(EmptySetupContext()) }
            .render(result)

        val expectedReport = readResourceContent("vanilla.sarif.json")
            .replace("<PREFIX>", Path(System.getProperty("user.dir")).toUri().toString())

        assertThat(report).isEqualToIgnoringWhitespace(expectedReport)
    }

    @Test
    fun `renders multiple issues with relative path`() {
        val basePath = "/Users/tester/detekt/"
        val result = TestDetektion(
            createFindingForRelativePath(ruleName = "TestSmellA", basePath = basePath),
            createFindingForRelativePath(ruleName = "TestSmellB", basePath = basePath),
            createFindingForRelativePath(ruleName = "TestSmellC", basePath = basePath)
        )

        val report = SarifOutputReport()
            .apply {
                init(
                    EmptySetupContext().apply {
                        register(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY, Path(basePath))
                    }
                )
            }
            .render(result)
            .stripWhitespace()

        val expectedReport = readResourceContent("relative_path.sarif.json")

        // Note: GitHub CI uses D: drive, but it could be any drive for local development
        val systemAwareExpectedReport = if (whichOS().startsWith("windows", ignoreCase = true)) {
            val winRoot = Path("/").absolutePathString().replace("\\", "/")
            expectedReport.replace("file:///", "file:///$winRoot")
        } else {
            expectedReport
        }

        assertThat(report).isEqualToIgnoringWhitespace(systemAwareExpectedReport)
    }

    @Test
    fun `region should be bounded with word`() {
        /**
         * Region constraints in Snippet for word 'TestClass'
         *  @sample Snippet.code
         */
        val startLine = 3
        val startColumn = 7
        val endLine = 3
        val endColumn = 15

        val refEntity = TestRule().compileAndLint(Snippet.code).first().entity
        val location = Location(
            source = SourceLocation(startLine, startColumn),
            text = TextLocation(
                startLine + (startColumn - 1) * Snippet.LINE_LENGTH,
                endColumn + (endLine - 1) * Snippet.LINE_LENGTH
            ),
            filePath = refEntity.location.filePath
        )

        val result = TestDetektion(
            createFinding(
                ruleName = "TestSmellB",
                entity = Entity(refEntity.name, refEntity.signature, location, refEntity.ktElement),
                severity = Severity.Warning
            )
        )

        val report = SarifOutputReport()
            .apply { init(EmptySetupContext()) }
            .render(result)

        assertThat(report)
            .containsIgnoringWhitespaces(constrainRegion(startLine, startColumn, endLine, endColumn))
    }

    @Test
    fun `region should be bounded with block`() {
        /**
         * Region constraints in Snippet for curly braces
         *  @sample Snippet.code
         */
        val startLine = 3
        val startColumn = 17
        val endLine = 5
        val endColumn = 1

        val refEntity = TestRule().compileAndLint(Snippet.code).first().entity
        val location = Location(
            source = SourceLocation(startLine, startColumn),
            text = TextLocation(
                startLine + (startColumn - 1) * Snippet.LINE_LENGTH,
                endColumn + (endLine - 1) * Snippet.LINE_LENGTH
            ),
            filePath = refEntity.location.filePath
        )

        val result = TestDetektion(
            createFinding(
                ruleName = "TestSmellB",
                entity = Entity(refEntity.name, refEntity.signature, location, refEntity.ktElement),
                severity = Severity.Warning
            )
        )

        val report = SarifOutputReport()
            .apply { init(EmptySetupContext()) }
            .render(result)

        assertThat(report)
            .containsIgnoringWhitespaces(constrainRegion(startLine, startColumn, endLine, endColumn))
    }
}

private object Snippet {
    // Each line of code is 50 chars long
    const val LINE_LENGTH = 50
    val code = """
        // 4567890123456789012345678901234567890123456789
        // 0000001111111111222222222233333333334444444444
        class TestClass { ///////////////////////////////
            val greeting: String = "Hello, World!"///////
        }////////////////////////////////////////////////
    """.trimIndent()
}

private fun constrainRegion(startLine: Int, startColumn: Int, endLine: Int, endColumn: Int) = """
    "region": {
      "endColumn": ${endColumn + 1},
      "endLine": $endLine,
      "startColumn": $startColumn,
      "startLine": $startLine
    }
""".trimIndent()

class TestProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("test")
    override fun instance(): RuleSet = RuleSet(ruleSetId, listOf(::TestRule))
}

class TestRule(config: Config = Config.empty) : Rule(config, "") {
    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        report(CodeSmell(issue, Entity.atName(classOrObject), message = "Error"))
    }
}

private fun String.stripWhitespace() = replace(Regex("\\s"), "")
