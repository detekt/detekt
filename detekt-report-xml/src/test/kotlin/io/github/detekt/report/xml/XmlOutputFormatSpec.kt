package io.github.detekt.report.xml

import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createFindingForRelativePath
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.Locale

private const val TAB = "\t"

class XmlOutputFormatSpec {

    private val entity1 = Entity(
        "Sample1",
        "",
        createLocation(
            path = "src/main/com/sample/Sample1.kt",
            position = 11 to 1,
        )
    )
    private val entity2 = Entity(
        "Sample2",
        "",
        createLocation(
            path = "src/main/com/sample/Sample2.kt",
            position = 22 to 2,
        )
    )
    private val outputFormat = XmlOutputReport()

    @Test
    fun `renders empty report`() {
        val result = outputFormat.render(TestDetektion())

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
        val smell = createFinding("id_a", entity1, "TestMessage")

        val result = outputFormat.render(TestDetektion(smell))

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="src/main/com/sample/Sample1.kt">
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.id_a" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders two reported issues in single file`() {
        val smell1 = createFinding("id_a", entity1, "TestMessage")
        val smell2 = createFinding("id_b", entity1, "TestMessage")

        val result = outputFormat.render(TestDetektion(smell1, smell2))

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="src/main/com/sample/Sample1.kt">
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.id_a" />
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.id_b" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders one reported issue across multiple files`() {
        val smell1 = createFinding("id_a", entity1, "TestMessage")
        val smell2 = createFinding("id_a", entity2, "TestMessage")

        val result = outputFormat.render(TestDetektion(smell1, smell2))

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="src/main/com/sample/Sample1.kt">
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.id_a" />
                </file>
                <file name="src/main/com/sample/Sample2.kt">
                $TAB<error line="22" column="2" severity="error" message="TestMessage" source="detekt.id_a" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders issues with relative path`() {
        val findingA = createFindingForRelativePath(
            ruleInfo = createRuleInfo("id_a"),
            basePath = "/Users/tester/detekt/",
            relativePath = "Sample1.kt"
        )
        val findingB = createFindingForRelativePath(
            ruleInfo = createRuleInfo("id_b"),
            basePath = "/Users/tester/detekt/",
            relativePath = "Sample2.kt"
        )

        val result = outputFormat.render(TestDetektion(findingA, findingB))

        assertThat(result).isEqualTo(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="Sample1.kt">
                $TAB<error line="1" column="1" severity="error" message="TestMessage" source="detekt.id_a" />
                </file>
                <file name="Sample2.kt">
                $TAB<error line="1" column="1" severity="error" message="TestMessage" source="detekt.id_b" />
                </file>
                </checkstyle>
            """.trimIndent()
        )
    }

    @Test
    fun `renders two reported issues across multiple files`() {
        val smell1 = createFinding("id_a", entity1, "TestMessage")
        val smell2 = createFinding("id_b", entity1, "TestMessage")
        val smell3 = createFinding("id_a", entity2, "TestMessage")
        val smell4 = createFinding("id_b", entity2, "TestMessage")

        val result = outputFormat.render(
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
                <file name="src/main/com/sample/Sample1.kt">
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.id_a" />
                $TAB<error line="11" column="1" severity="error" message="TestMessage" source="detekt.id_b" />
                </file>
                <file name="src/main/com/sample/Sample2.kt">
                $TAB<error line="22" column="2" severity="error" message="TestMessage" source="detekt.id_a" />
                $TAB<error line="22" column="2" severity="error" message="TestMessage" source="detekt.id_b" />
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
            val finding = createFinding(
                ruleName = "issue_id",
                entity = entity1,
                message = "message",
                severity = severity,
            )

            val expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <checkstyle version="4.3">
                <file name="src/main/com/sample/Sample1.kt">
                $TAB<error line="${finding.location.source.line}" column="${finding.location.source.column}" severity="$xmlSeverity" message="${finding.message}" source="detekt.${finding.ruleInfo.id}" />
                </file>
                </checkstyle>
            """.trimIndent()

            val actual = outputFormat.render(TestDetektion(finding))

            assertThat(actual).isEqualTo(expected)
        }
    }
}
