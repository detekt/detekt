package io.github.detekt.report.md

import io.github.detekt.metrics.CognitiveComplexity
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.github.detekt.test.utils.internal.FakeKtElement
import io.github.detekt.test.utils.internal.FakePsiFile
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.TestSetupContext
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtElement
import org.junit.jupiter.api.Test

class MdOutputReportSpec {
    private val mdReport = MdOutputReport().apply { init(TestSetupContext()) }
    private val detektion = createTestDetektionWithMultipleSmells()
    private val result = mdReport.render(detektion)

    @Test
    fun `renders Markdown structure correctly`() {
        assertThat(result).contains("Metrics")
        assertThat(result).contains("Complexity Report")
        assertThat(result).contains("Issues")
    }

    @Test
    fun `contains zero issues`() {
        val result = mdReport.render(TestDetektion())

        assertThat(result).contains("Issues (0)")
    }

    @Test
    fun `contains the total number of issues`() {
        assertThat(result).contains("Issues (3)")
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
        assertThat(result).contains("rule_a/id (2)")
        assertThat(result).contains("rule_b (1)")
    }

    @Test
    fun `renders the right violation messages for the rules`() {
        assertThat(result).contains("Issue message 1")
        assertThat(result).contains("Issue message 2")
    }

    @Test
    fun `renders the right violation description for the rules`() {
        assertThat(result).contains("Description rule_a")
        assertThat(result).contains("Description rule_b")
    }

    @Test
    fun `renders the right documentation links for the rules`() {
        val detektion = TestDetektion(
            createIssue(createRuleInstance("ValCouldBeVar", "Style")),
            createIssue(createRuleInstance("EmptyBody", "empty")),
            createIssue(createRuleInstance("EmptyIf", "empty")),
        )

        val result = mdReport.render(detektion)
        assertThat(result).contains("[Documentation](https://detekt.dev/docs/rules/style#valcouldbevar)")
        assertThat(result).contains("[Documentation](https://detekt.dev/docs/rules/empty#emptybody)")
        assertThat(result).contains("[Documentation](https://detekt.dev/docs/rules/empty#emptyif)")
    }

    @Test
    fun `asserts that the generated md is the same even if we change the order of the issues`() {
        val issues = issues()
        val reversedIssues = issues.reversedArray()

        val firstDetektion = createMdDetektion(*issues)
        val secondDetektion = createMdDetektion(*reversedIssues)

        val firstReport = mdReport.render(firstDetektion)
        val secondReport = mdReport.render(secondDetektion)

        assertThat(firstReport).isEqualTo(secondReport)
    }
}

private fun fakeKtElement(): KtElement {
    @Language("kotlin")
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
    val fakePsiFile = FakePsiFile(code)
    val fakeKtElement = FakeKtElement(fakePsiFile)

    return fakeKtElement
}

private fun createTestDetektionWithMultipleSmells(): Detektion {
    val entity1 = createEntity(
        location = createLocation(
            path = "src/main/com/sample/Sample1.kt",
            basePath = "Users/tester/detekt/",
            position = 9 to 17,
            text = 17..20,
        ),
        ktElement = fakeKtElement(),
    )
    val entity2 = createEntity(
        location = createLocation(
            path = "src/main/com/sample/Sample2.kt",
            basePath = "Users/tester/detekt/",
            position = 13 to 17,
        ),
        ktElement = fakeKtElement(),
    )
    val entity3 = createEntity(
        location = createLocation(
            path = "src/main/com/sample/Sample3.kt",
            basePath = "Users/tester/detekt/",
            position = 14 to 16,
        ),
        ktElement = fakeKtElement(),
    )

    return createMdDetektion(
        createIssue(createRuleInstance("rule_a/id", "Section-1"), entity1, "Issue message 1"),
        createIssue(createRuleInstance("rule_a/id", "Section-1"), entity2, "Issue message 2"),
        createIssue(createRuleInstance("rule_b", "Section-2"), entity3, "Issue message 3"),
    ).also {
        it.putUserData(complexityKey, 10)
        it.putUserData(CognitiveComplexity.KEY, 10)
        it.putUserData(sourceLinesKey, 20)
        it.putUserData(logicalLinesKey, 10)
        it.putUserData(commentLinesKey, 2)
        it.putUserData(linesKey, 2222)
    }
}

private fun createMdDetektion(vararg issues: Issue): Detektion =
    TestDetektion(
        *issues,
        metrics = listOf(ProjectMetric("M1", 10_000), ProjectMetric("M2", 2))
    )

private fun issues(): Array<Issue> {
    val entity1 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 11 to 5))
    val entity2 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 22 to 2))
    val entity3 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 11 to 2))
    val entity4 = createEntity(location = createLocation("src/main/com/sample/Sample2.kt", position = 1 to 1))

    return arrayOf(
        createIssue(createRuleInstance("rule_a", "RuleSet1"), entity1),
        createIssue(createRuleInstance("rule_a", "RuleSet1"), entity2),
        createIssue(createRuleInstance("rule_a", "RuleSet1"), entity3),
        createIssue(createRuleInstance("rule_a", "RuleSet1"), entity4),
        createIssue(createRuleInstance("rule_b", "RuleSet1"), entity2),
        createIssue(createRuleInstance("rule_b", "RuleSet1"), entity1),
        createIssue(createRuleInstance("rule_b", "RuleSet1"), entity4),
        createIssue(createRuleInstance("rule_b", "RuleSet2"), entity3),
        createIssue(createRuleInstance("rule_c", "RuleSet2"), entity1),
        createIssue(createRuleInstance("rule_c", "RuleSet2"), entity2),
    )
}
