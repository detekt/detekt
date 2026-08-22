package dev.detekt.core.extensions

import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.RuleInstance
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueLocation
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.core.createProcessingSettings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReportingSpec {

    @Test
    fun `sorts issues by path and position`() {
        val rule = createRuleInstance("TestRule")
        val issues = listOf(
            issue(rule, "B.kt", line = 2, message = "second in B"),
            issue(rule, "A.kt", line = 5, message = "second in A"),
            issue(rule, "B.kt", line = 1, message = "first in B"),
            issue(rule, "A.kt", line = 1, message = "first in A"),
        )

        assertThat(report(issues, listOf(rule)).map { it.message })
            .containsExactly("first in A", "second in A", "first in B", "second in B")
    }

    @Test
    fun `sorts issues at the same position by rule id and message`() {
        val alpha = createRuleInstance("AlphaRule")
        val zulu = createRuleInstance("ZuluRule")
        val issues = listOf(
            issue(zulu, "A.kt", line = 1, message = "b"),
            issue(alpha, "A.kt", line = 1, message = "b"),
            issue(zulu, "A.kt", line = 1, message = "a"),
            issue(alpha, "A.kt", line = 1, message = "a"),
        )

        assertThat(report(issues, listOf(alpha, zulu)).map { "${it.ruleInstance.id}: ${it.message}" })
            .containsExactly("AlphaRule: a", "AlphaRule: b", "ZuluRule: a", "ZuluRule: b")
    }

    private fun issue(rule: RuleInstance, path: String, line: Int, message: String): Issue =
        createIssue(
            ruleInstance = rule,
            location = createIssueLocation(path = path, position = line to 1, endPosition = line to 10),
            message = message,
        )

    private fun report(issues: List<Issue>, rules: List<RuleInstance>): List<Issue> =
        createProcessingSettings().use { handleReportingExtensions(it, Detektion(issues, rules)).issues }
}
