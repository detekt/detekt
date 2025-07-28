package dev.detekt.core.reporting.console

import dev.detekt.core.reporting.SuppressedIssueAssert
import dev.detekt.api.test.TestDetektion
import dev.detekt.api.test.TestSetupContext
import dev.detekt.api.test.createIssue
import dev.detekt.api.test.createLocation
import dev.detekt.api.test.createRuleInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class LiteIssuesReportSpec {
    private val basePath = Path("").absolute()
    private val subject = LiteIssuesReport().apply { init(TestSetupContext(basePath = basePath)) }

    @Test
    fun `reports non-empty issues`() {
        val location = createLocation()
        val detektion = TestDetektion(
            createIssue(createRuleInstance("SpacingAfterPackageDeclaration/id"), location),
            createIssue(createRuleInstance("UnnecessarySafeCall"), location),
        )
        assertThat(subject.render(detektion)).isEqualTo(
            """
                ${basePath.resolve(location.path)}:1:1: TestMessage [SpacingAfterPackageDeclaration/id]
                ${basePath.resolve(location.path)}:1:1: TestMessage [UnnecessarySafeCall]

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
