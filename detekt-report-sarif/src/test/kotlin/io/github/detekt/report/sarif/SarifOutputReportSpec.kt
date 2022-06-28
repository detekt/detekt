package io.github.detekt.report.sarif

import io.github.detekt.test.utils.readResourceContent
import io.github.detekt.tooling.api.VersionProvider
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.test.EmptySetupContext
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createFindingForRelativePath
import io.gitlab.arturbosch.detekt.test.createIssue
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtElement
import org.junit.jupiter.api.Test
import java.nio.file.Paths

@OptIn(UnstableApi::class)
class SarifOutputReportSpec {

    @Test
    fun `renders multiple issues`() {
        val result = TestDetektion(
            createFinding(ruleName = "TestSmellA", severity = SeverityLevel.ERROR),
            createFinding(ruleName = "TestSmellB", severity = SeverityLevel.WARNING),
            createFinding(ruleName = "TestSmellC", severity = SeverityLevel.INFO)
        )

        val report = SarifOutputReport()
            .apply { init(EmptySetupContext()) }
            .render(result)

        assertThat(report).isEqualToIgnoringWhitespace(readResourceContent("vanilla.sarif.json"))
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
                        register(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY, Paths.get(basePath))
                    }
                )
            }
            .render(result)
            .stripWhitespace()

        val expectedReport = readResourceContent("relative_path.sarif.json")

        // Note: Github CI uses D: drive, but it could be any drive for local development
        val systemAwareExpectedReport = if (whichOS().startsWith("windows", ignoreCase = true)) {
            val winRoot = Paths.get("/").toAbsolutePath().toString().replace("\\", "/")
            expectedReport.replace("file:///", "file://$winRoot")
        } else {
            expectedReport
        }

        assertThat(report).isEqualToIgnoringWhitespace(systemAwareExpectedReport)
    }

    @Test
    fun `snippet region should be bounded with word`() {
        val entity = createEntity(
            path = "TestFile.kt",
            position = 3 to 7,
            text = 33..41,
            ktElement = mockKtElement(),
        )

        val result = TestDetektion(
            createFinding(ruleName = "TestSmellB", entity = entity, severity = SeverityLevel.WARNING),
        )

        val report = SarifOutputReport()
            .apply { init(EmptySetupContext()) }
            .render(result)

        val boundedRegion = """
            "region": {
              "endColumn": 16,
              "endLine": 3,
              "startColumn": 7,
              "startLine": 3
            }            
        """
        assertThat(report).containsIgnoringWhitespaces(boundedRegion)
    }

    @Test
    fun `snippet region should be bounded with block`() {
        val entity = createEntity(
            path = "TestFile.kt",
            position = 3 to 7,
            text = 33..88,
            ktElement = mockKtElement(),
        )

        val result = TestDetektion(
            createFinding(ruleName = "TestSmellB", entity = entity, severity = SeverityLevel.WARNING),
        )

        val report = SarifOutputReport()
            .apply { init(EmptySetupContext()) }
            .render(result)

        val boundedRegion = """
            "region": {
              "endColumn": 2,
              "endLine": 5,
              "startColumn": 7,
              "startLine": 3
            }            
        """
        assertThat(report).containsIgnoringWhitespaces(boundedRegion)
    }
}

private fun mockKtElement(): KtElement {
    val ktElementMock = mockk<KtElement>()
    val psiFileMock = mockk<PsiFile>()
    val code = """
        package com.example.test

        class testClass {
            val greeting: String = "Hello, World!"
        }
        
    """.trimIndent()

    every { psiFileMock.text } returns code
    every { ktElementMock.containingFile } returns psiFileMock
    return ktElementMock
}

private fun createFinding(ruleName: String, severity: SeverityLevel, entity: Entity? = null): Finding {
    return object : CodeSmell(
        createIssue(ruleName),
        entity ?: createEntity("TestFile.kt"),
        "TestMessage"
    ) {
        override val severity: SeverityLevel
            get() = severity
    }
}

private fun String.stripWhitespace() = replace(Regex("\\s"), "")

internal class TestVersionProvider : VersionProvider {

    override fun current(): String = "1.0.0"
}
