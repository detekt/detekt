package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.core.reporting.SuppressedIssueAssert
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.TestSetupContext
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
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
