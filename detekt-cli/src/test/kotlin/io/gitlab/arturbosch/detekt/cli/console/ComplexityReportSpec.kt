package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.processors.commentLinesKey
import io.gitlab.arturbosch.detekt.core.processors.complexityKey
import io.gitlab.arturbosch.detekt.core.processors.linesKey
import io.gitlab.arturbosch.detekt.core.processors.logicalLinesKey
import io.gitlab.arturbosch.detekt.core.processors.sourceLinesKey
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

internal class ComplexityReportSpec : SubjectSpek<ComplexityReport>({

	subject { ComplexityReport() }

	given("several complexity metrics") {

		val reportText = """
			Complexity Report:
				- 10 lines of code (loc)
				- 6 source lines of code (sloc)
				- 5 logical lines of code (lloc)
				- 4 comment lines of code (cloc)
				- 2 McCabe complexity (mcc)
				- 1 number of total code smells
				- 66 % comment source ratio
				- 400 mcc per 1000 lloc
				- 200 code smells per 1000 lloc"""

		it("successfully generates a complexity report") {
			val detektion = createDetektion()
			addData(detektion)
			assertThat(generateComplexityReport(detektion)).isEqualToIgnoringWhitespace(reportText)
		}

		it("returns null for missing complexity metrics") {
			val detektion = createDetektion()
			assertThat(generateComplexityReport(detektion)).isNull()
		}

		it("returns null for missing complexity metrics in report") {
			val report = ComplexityReport()
			val detektion = createDetektion()
			assertThat(report.render(detektion)).isNull()
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

private fun generateComplexityReport(detektion: Detektion): String? {
	val complexityMetric = ComplexityMetric(detektion)
	val generator = ComplexityReportGenerator(complexityMetric)
	return generator.generate()
}
