package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class ComplexityReportSpec : Spek({

    describe("complexity report") {

        context("several complexity metrics") {

            it("successfully generates a complexity report") {
                val report = ComplexityReport()
                val expectedContent = readResource("complexity-report.txt")
                val detektion = createDetektion()
                addData(detektion)
                // Casting expectedContent to Any is workaround for
                // https://github.com/joel-costigliola/assertj-core/issues/1440#issuecomment-465032464
                assertThat(report.render(detektion)).isEqualTo(expectedContent as Any)
            }

            it("returns null for missing complexity metrics in report") {
                val report = ComplexityReport()
                val detektion = createDetektion()
                assertThat(report.render(detektion)).isNull()
            }
        }
    }
})

private fun createDetektion(): Detektion = DetektResult(mapOf(Pair("Key", listOf(createFinding()))))

private fun addData(detektion: Detektion) {
    detektion.addData(complexityKey, 2)
    detektion.addData(linesKey, 10)
    detektion.addData(sourceLinesKey, 6)
    detektion.addData(logicalLinesKey, 5)
    detektion.addData(commentLinesKey, 4)
}
