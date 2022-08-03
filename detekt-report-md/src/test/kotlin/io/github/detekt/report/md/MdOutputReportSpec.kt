package io.github.detekt.report.md

import io.github.detekt.metrics.CognitiveComplexity
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createIssue
import io.mockk.clearStaticMockk
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtElement
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MdOutputReportSpec {
    private val mdReport = MdOutputReport()
    private val detektion = createTestDetektionWithMultipleSmells()
    private val result = mdReport.render(detektion)

    @BeforeEach
    fun setup() {
        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now(ZoneOffset.UTC) } returns OffsetDateTime.of(
            2000, // year
            1, // month
            1, // dayOfMonth
            0, // hour
            0, // minute
            0, // second
            0, // nanoOfSecond
            ZoneOffset.UTC // offset
        )
    }

    @AfterEach
    fun teardown() {
        clearStaticMockk(OffsetDateTime::class)
    }

    @Test
    fun `renders Markdown structure correctly`() {
        assertThat(result).contains("Metrics")
        assertThat(result).contains("Complexity Report")
        assertThat(result).contains("Findings")
    }

    @Test
    fun `contains zero findings`() {
        val result = mdReport.render(TestDetektion())

        assertThat(result).contains("Findings (0)")
    }

    @Test
    fun `contains the total number of findings`() {
        assertThat(result).contains("Findings (3)")
    }

    @Test
    fun `renders the 'generated with' text correctly`() {
        val header = "generated with [detekt version ${whichDetekt()}](https://detekt.dev/) on "

        assertThat(result).contains(header)
    }

    @Test
    fun `renders the right file locations`() {
        assertThat(result).contains("src/main/com/sample/Sample1.kt:9:17")
        assertThat(result).contains("src/main/com/sample/Sample2.kt:13:17")
        assertThat(result).contains("src/main/com/sample/Sample3.kt:14:16")
    }

    @Test
    fun `renders the right number of issues per rule`() {
        assertThat(result).contains("id_a (2)")
        assertThat(result).contains("id_b (1)")
    }

    @Test
    fun `renders the right violation messages for the rules`() {
        assertThat(result).contains("Message finding 1")
        assertThat(result).contains("Message finding 2")
    }

    @Test
    fun `renders the right violation description for the rules`() {
        assertThat(result).contains("Description id_a")
        assertThat(result).contains("Description id_b")
    }

    @Test
    fun `renders the right documentation links for the rules`() {
        val detektion = object : TestDetektion() {
            override val findings: Map<String, List<Finding>> = mapOf(
                "Style" to listOf(
                    createFinding(createIssue("ValCouldBeVar"), createEntity(""))
                ),
                "empty" to listOf(
                    createFinding(createIssue("EmptyBody"), createEntity("")),
                    createFinding(createIssue("EmptyIf"), createEntity(""))
                )
            )
        }

        val result = mdReport.render(detektion)
        assertThat(result).contains("[Documentation](https://detekt.dev/docs/rules/style#valcouldbevar)")
        assertThat(result).contains("[Documentation](https://detekt.dev/docs/rules/empty#emptybody)")
        assertThat(result).contains("[Documentation](https://detekt.dev/docs/rules/empty#emptyif)")
    }

    @Test
    fun `asserts that the generated HTML is the same even if we change the order of the findings`() {
        val findings = findings()
        val reversedFindings = findings
            .reversedArray()
            .map { (section, findings) -> section to findings.asReversed() }
            .toTypedArray()

        val firstDetektion = createMdDetektion(*findings)
        val secondDetektion = createMdDetektion(*reversedFindings)

        val firstReport = mdReport.render(firstDetektion)
        val secondReport = mdReport.render(secondDetektion)

        assertThat(firstReport).isEqualTo(secondReport)
    }
}

private fun mockKtElement(): KtElement {
    val ktElementMock = mockk<KtElement>()
    val psiFileMock = mockk<PsiFile>()
    val code = """
        package com.example.test
        
        import io.github.*
        
        class Test() {
            val greeting: String = "Hello, World!"

            init {
                println(greetings)
            }
            
            fun foo() {
                println(greetings)
                return this
            }
        }
    """.trimIndent()

    every { psiFileMock.text } returns code
    every { ktElementMock.containingFile } returns psiFileMock
    return ktElementMock
}

private fun createTestDetektionWithMultipleSmells(): Detektion {
    val entity1 = createEntity(
        path = "src/main/com/sample/Sample1.kt",
        position = 9 to 17,
        text = 17..20,
        ktElement = mockKtElement(),
        basePath = "/Users/tester/detekt/"
    )
    val entity2 = createEntity(
        path = "src/main/com/sample/Sample2.kt",
        ktElement = mockKtElement(),
        position = 13 to 17,
        basePath = "/Users/tester/detekt/"
    )
    val entity3 = createEntity(
        path = "src/main/com/sample/Sample3.kt",
        position = 14 to 16,
        ktElement = mockKtElement(),
        basePath = "/Users/tester/detekt/"
    )

    val issueA = createIssue("id_a")
    val issueB = createIssue("id_b")

    return createMdDetektion(
        "Section-1" to listOf(
            createFinding(issueA, entity1, "Message finding 1"),
            createFinding(issueA, entity2, "Message finding 2")
        ),
        "Section-2" to listOf(
            createFinding(issueB, entity3, "")
        )
    ).also {
        it.addData(complexityKey, 10)
        it.addData(CognitiveComplexity.KEY, 10)
        it.addData(sourceLinesKey, 20)
        it.addData(logicalLinesKey, 10)
        it.addData(commentLinesKey, 2)
        it.addData(linesKey, 2222)
    }
}

private fun createMdDetektion(vararg findingPairs: Pair<String, List<Finding>>): Detektion {
    return object : TestDetektion() {
        override val findings: Map<String, List<Finding>> = findingPairs.toMap()

        override val metrics: Collection<ProjectMetric> = listOf(
            ProjectMetric("M1", 10_000),
            ProjectMetric("M2", 2)
        )
    }
}

private fun findings(): Array<Pair<String, List<Finding>>> {
    val issueA = createIssue("id_a")
    val issueB = createIssue("id_b")
    val issueC = createIssue("id_c")

    val entity1 = createEntity("src/main/com/sample/Sample1.kt", 11 to 5)
    val entity2 = createEntity("src/main/com/sample/Sample1.kt", 22 to 2)
    val entity3 = createEntity("src/main/com/sample/Sample1.kt", 11 to 2)
    val entity4 = createEntity("src/main/com/sample/Sample2.kt", 1 to 1)

    return arrayOf(
        "Section 1" to listOf(
            createFinding(issueA, entity1),
            createFinding(issueA, entity2),
            createFinding(issueA, entity3),
            createFinding(issueA, entity4),
            createFinding(issueB, entity2),
            createFinding(issueB, entity1),
            createFinding(issueB, entity4)
        ),
        "Section 2" to listOf(
            createFinding(issueB, entity3),
            createFinding(issueC, entity1),
            createFinding(issueC, entity2)
        )
    )
}
