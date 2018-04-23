package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlainOutputReportTest {

	@Test
	fun render() {
		val report = PlainOutputReport()
		val detektion = TestDetektion(createFinding())
		val renderedText = "TestSmell - [TestEntity] at TestFile.kt:1:1 - Signature=S1"
		assertThat(report.render(detektion)).isEqualTo(renderedText)
	}
}
