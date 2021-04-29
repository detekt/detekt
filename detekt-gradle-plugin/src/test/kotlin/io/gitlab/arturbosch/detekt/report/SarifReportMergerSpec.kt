package io.gitlab.arturbosch.detekt.report

import io.github.detekt.test.utils.resourceAsFile
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

internal class SarifReportMergerSpec : Spek({
    describe("sarif report merger") {
        it("merges input into output successfully") {
            val input1 = resourceAsFile("input_1.sarif.json")
            val input2 = resourceAsFile("input_2.sarif.json")
            val output = File.createTempFile("output", "xml")
            SarifReportMerger.merge(listOf(input1, input2), output)

            val expectedOutput = resourceAsFile("output.sarif.json")
            assertThat(output.readText()).isEqualToNormalizingNewlines(expectedOutput.readText())
        }
    }
})
