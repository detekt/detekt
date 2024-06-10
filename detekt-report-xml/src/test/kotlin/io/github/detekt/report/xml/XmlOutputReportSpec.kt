package io.github.detekt.report.xml

import io.github.detekt.test.utils.internal.FakeKtElement
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createIssueForRelativePath
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.Locale
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.invariantSeparatorsPathString

private const val TAB = "\t"

class XmlOutputReportSpec {

    private val entity1 = Entity(
        "Sample1",
        "",
        createLocation(
            path = "src/main/com/sample/Sample1.kt",
            position = 11 to 1,
        ),
        FakeKtElement()
    )
    private val entity2 = Entity(
        "Sample2",
        "",
        createLocation(
            path = "src/main/com/sample/Sample2.kt",
            position = 22 to 2,
        ),
        FakeKtElement()
    )
    private val outputReport = XmlOutputReport()

    @Test
    fun `renders empty report`() {
        val result = outputReport.render(TestDetektion())

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders one reported issue in single file`() {
        val smell = createIssue("rule_a/id", entity1, "TestMessage")

        val result = outputReport.render(TestDetektion(smell))

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="${entity1.location.path.invariantSeparatorsPathString}">
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.rule_a/id" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders two reported issues in single file`() {
        val smell1 = createIssue("rule_a/id", entity1, "TestMessage")
        val smell2 = createIssue("rule_b", entity1, "TestMessage")

        val result = outputReport.render(TestDetektion(smell1, smell2))

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="${entity1.location.path.invariantSeparatorsPathString}">
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.rule_a/id" />
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.rule_b" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders one reported issue across multiple files`() {
        val smell1 = createIssue("rule_a/id", entity1, "TestMessage")
        val smell2 = createIssue("rule_a/id", entity2, "TestMessage")

        val result = outputReport.render(TestDetektion(smell1, smell2))

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="${entity1.location.path.invariantSeparatorsPathString}">
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.rule_a/id" />
                </file>
                <file name="${entity2.location.path.invariantSeparatorsPathString}">
                $TAB<error line="22" column="2" severity="error" message="TestMessage" source="detekt.rule_a/id" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders issues with relative path`() {
        val issueA = createIssueForRelativePath(
            ruleInstance = createRuleInstance("rule_a/id"),
            basePath = "${System.getProperty("user.dir")}/Users/tester/detekt/",
            relativePath = "Sample1.kt"
        )
        val issueB = createIssueForRelativePath(
            ruleInstance = createRuleInstance("rule_b"),
            basePath = "${System.getProperty("user.dir")}/Users/tester/detekt/",
            relativePath = "Sample2.kt"
        )

        val outputReport = XmlOutputReport()
        outputReport.basePath = Path("Users/tester/detekt/").absolute()

        val result = outputReport.render(TestDetektion(issueA, issueB))

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="Sample1.kt">
                $TAB<error line="1" column="1" severity="error" message="TestMessage" source="detekt.rule_a/id" />
                </file>
                <file name="Sample2.kt">
                $TAB<error line="1" column="1" severity="error" message="TestMessage" source="detekt.rule_b" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders two reported issues across multiple files`() {
        val smell1 = createIssue("rule_a/id", entity1, "TestMessage")
        val smell2 = createIssue("rule_b", entity1, "TestMessage")
        val smell3 = createIssue("rule_a/id", entity2, "TestMessage")
        val smell4 = createIssue("rule_b", entity2, "TestMessage")

        val result = outputReport.render(
            TestDetektion(
                smell1,
                smell2,
                smell3,
                smell4
            )
        )

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="${entity1.location.path.invariantSeparatorsPathString}">
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.rule_a/id" />
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.rule_b" />
                </file>
                <file name="${entity2.location.path.invariantSeparatorsPathString}">
                $TAB<error line="22" column="2" severity="error" message="TestMessage" source="detekt.rule_a/id" />
                $TAB<error line="22" column="2" severity="error" message="TestMessage" source="detekt.rule_b" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Nested
    inner class `severity level conversion` {

        @ParameterizedTest
        @EnumSource(Severity::class)
        fun `renders detektion with severity as XML with severity`(severity: Severity) {
            val xmlSeverity = severity.name.lowercase(Locale.US)
            val issue = createIssue(
                ruleId = "issue/id",
                entity = entity1,
                message = "message",
                severity = severity,
            )

            val expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="${entity1.location.path.invariantSeparatorsPathString}">
                $TAB<error line="${issue.location.source.line}" column="${issue.location.source.column}" severity="$xmlSeverity" message="${issue.message}" source="detekt.${issue.ruleInstance.id}" />
                </file>
                </checkstyle>
            """.trimIndent()

            val actual = outputReport.render(TestDetektion(issue))

            assertThat(actual).isEqualTo(expected)
        }
    }
}
