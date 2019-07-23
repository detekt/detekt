package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TxtOutputReportTest : Spek({

    describe("TXT output report") {

        it("render") {
            val report = TxtOutputReport()
            val detektion = TestDetektion(createFinding())
            val renderedText = "TestSmell - [TestEntity] at TestFile.kt:1:1 - Signature=S1"
            assertThat(report.render(detektion)).isEqualTo(renderedText)
        }
    }
})
