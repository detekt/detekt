package dev.detekt.api

import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createRuleInstance
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class DetektionSpec {

    @Test
    fun `doesn't fail with both empty`() {
        Detektion(
            issues = emptyList(),
            rules = emptyList(),
        )
    }

    @Test
    fun `doesn't fail with empty issues`() {
        Detektion(
            emptyList(),
            rules,
        )
    }

    @Test
    fun `doesn't fail with 2 issues of the same rule`() {
        Detektion(
            listOf(
                createIssue(createRuleInstance("0")),
                createIssue(createRuleInstance("0")),
            ),
            rules,
        )
    }

    @Test
    fun `fail with an issue with an unknown rule`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy {
                Detektion(
                    listOf(
                        createIssue(createRuleInstance("0")),
                        createIssue(createRuleInstance("1")),
                        createIssue(createRuleInstance("Unknown")),
                    ),
                    rules,
                )
            }
            .withMessage("The rule Unknown was not reported as having been executed")
    }

    @Test
    fun `fail with more than one issue with an unknown rule`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy {
                Detektion(
                    listOf(
                        createIssue(createRuleInstance("0")),
                        createIssue(createRuleInstance("1")),
                        createIssue(createRuleInstance("Unknown")),
                        createIssue(createRuleInstance("Unknown2")),
                    ),
                    rules,
                )
            }
            .withMessage("The rules [Unknown, Unknown2] were not reported as having been executed")
    }
}

private val rules = listOf(
    createRuleInstance("0"),
    createRuleInstance("1"),
    createRuleInstance("2"),
    createRuleInstance("3"),
)
