package io.github.detekt.report.sarif

import io.github.detekt.test.utils.readResourceContent
import io.github.detekt.tooling.api.VersionProvider
import io.gitlab.arturbosch.detekt.test.EmptySetupContext
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SarifOutputReportSpec : Spek({

    describe("sarif output report") {

        val expectedReport by memoized {
            readResourceContent("expected.sarif.json").stripWhitespace()
        }

        it("renders multiple issues") {
            val result = TestDetektion(
                createFinding(ruleName = "TestSmellA"),
                createFinding(ruleName = "TestSmellB"),
                createFinding(ruleName = "TestSmellC")
            )

            val report = SarifOutputReport().apply { init(EmptySetupContext()) }
                .render(result)
                .stripWhitespace()

            assertThat(report).isEqualTo(expectedReport)
        }
    }
})

internal fun String.stripWhitespace() = replace(Regex("\\s"), "")

internal class TestVersionProvider : VersionProvider {

    override fun current(): String = "1.0.0"
}
