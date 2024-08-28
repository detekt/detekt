package io.github.detekt.report.md

import io.github.detekt.metrics.CognitiveComplexity
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
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
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class MdOutputReportSpec {
    private val basePath = Path("src/test/resources").absolute()
    private val mdReport = MdOutputReport().apply { init(TestSetupContext(basePath = basePath)) }
    private val detektion = createTestDetektionWithMultipleSmells()
    private val result = mdReport.render(detektion)
        .replace("""\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d UTC""".toRegex(), "2024-07-21 21:34:16 UTC")
        .replace("""\[detekt version \d+\.\d+.\d+]""".toRegex(), "[detekt version 1.23.6]")

    @Suppress("LongMethod")
    @Test
    fun checkAll() {
        assertThat(result).isEqualTo(
            """
                # detekt
                
                ## Metrics
                
                * 10,000 M1
                
                * 2 M2
                
                ## Complexity Report
                
                * 2,222 lines of code (loc)
                
                * 20 source lines of code (sloc)
                
                * 10 logical lines of code (lloc)
                
                * 2 comment lines of code (cloc)
                
                * 10 cyclomatic complexity (mcc)
                
                * 10 cognitive complexity
                
                * 3 number of total code smells
                
                * 10% comment source ratio
                
                * 1,000 mcc per 1,000 lloc
                
                * 300 code smells per 1,000 lloc
                
                ## Issues (3)
                
                ### Section-1, rule_a/id (2)
                
                Description rule_a
                
                [Documentation](https://detekt.dev/docs/rules/section-1#rule_a)
                
                * src/main/com/sample/Sample1.kt:9:17
                ```
                Issue message 1
                ```
                ```kotlin
                6      val greeting: String = "Hello, World!"
                7  
                8      init {
                9          println(greetings)
                !                  ^ error
                10     }
                11 
                12     fun foo() {
                
                ```
                
                * src/main/com/sample/Sample2.kt:13:17
                ```
                Issue message 2
                ```
                ```kotlin
                10     }
                11 
                12     fun foo() {
                13         println(greetings)
                !!                 ^ error
                14         return this
                15     }
                16 }
                
                ```
                
                ### Section-2, rule_b (1)
                
                Description rule_b
                
                [Documentation](https://detekt.dev/docs/rules/section-2#rule_b)
                
                * src/main/com/sample/Sample3.kt:14:16
                ```
                Issue message 3
                ```
                ```kotlin
                11 
                12     fun foo() {
                13         println(greetings)
                14         return this
                !!                ^ error
                15     }
                16 }
                
                ```
                
                generated with [detekt version 1.23.6](https://detekt.dev/) on 2024-07-21 21:34:16 UTC
                
            """.trimIndent()
        )
    }

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

private fun createTestDetektionWithMultipleSmells(): Detektion {
    val entity1 = createEntity(
        location = createLocation(path = "src/main/com/sample/Sample1.kt", position = 9 to 17, text = 17..20),
    )
    val entity2 = createEntity(
        location = createLocation(path = "src/main/com/sample/Sample2.kt", position = 13 to 17),
    )
    val entity3 = createEntity(
        location = createLocation(path = "src/main/com/sample/Sample3.kt", position = 14 to 16),
    )

    return createMdDetektion(
        createIssue(createRuleInstance("rule_a/id", "Section-1"), entity1, "Issue message 1"),
        createIssue(createRuleInstance("rule_a/id", "Section-1"), entity2, "Issue message 2"),
        createIssue(createRuleInstance("rule_b", "Section-2"), entity3, "Issue message 3"),
        createIssue(
            createRuleInstance("rule_c", "Section-2"),
            entity3,
            "Issue message 3",
            suppressReasons = listOf("suppress")
        ),
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
