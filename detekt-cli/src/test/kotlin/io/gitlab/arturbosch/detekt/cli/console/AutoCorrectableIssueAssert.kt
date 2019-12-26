package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.cli.createCorrectableFinding
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat

internal object AutoCorrectableIssueAssert {

    fun isReportNull(report: ConsoleReport) {
        val config = TestConfig(mapOf("maxIssues" to "0", "excludeCorrectable" to "true"))
        report.init(config)
        val correctableCodeSmell = createCorrectableFinding()
        val detektionWithCorrectableSmell = TestDetektion(correctableCodeSmell)
        val result = report.render(detektionWithCorrectableSmell)
        assertThat(result).isNull()
    }
}
