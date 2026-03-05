package dev.detekt.core.reporting.console

import dev.detekt.api.Severity.Error
import dev.detekt.api.Severity.Info
import dev.detekt.api.Severity.Warning
import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.api.testfixtures.TestSetupContext
import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueLocation
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.core.reporting.SuppressedIssueAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class LiteIssuesReportSpec {
    private val basePath = Path("").absolute()
    private val subject = LiteIssuesReport().apply { init(TestSetupContext(basePath = basePath)) }

    @Test
    fun `reports non-empty issues`() {
        val location = createIssueLocation()
        val detektion = TestDetektion(
            createIssue(createRuleInstance("SpacingAfterPackageDeclaration/id"), location, severity = Error),
            createIssue(createRuleInstance("UnnecessarySafeCall"), location, severity = Warning),
            createIssue(createRuleInstance("MagicNumber"), location, severity = Info),
        )
        assertThat(subject.render(detektion)).isEqualTo(
            """
                e: ${basePath.resolve(location.path)}:1:1 TestMessage [SpacingAfterPackageDeclaration/id]
                w: ${basePath.resolve(location.path)}:1:1 TestMessage [UnnecessarySafeCall]
                i: ${basePath.resolve(location.path)}:1:1 TestMessage [MagicNumber]

            """.trimIndent()
        )
    }

    @Test
    fun `reports no issues`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }

    @Test
    fun `should not add auto corrected issues to report`() {
        val report = LiteIssuesReport()
        SuppressedIssueAssert.isReportNull(report)
    }
}
