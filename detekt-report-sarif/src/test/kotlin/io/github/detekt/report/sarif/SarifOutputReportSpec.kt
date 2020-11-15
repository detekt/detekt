package io.github.detekt.report.sarif

import io.github.detekt.tooling.api.VersionProvider
import io.gitlab.arturbosch.detekt.test.EmptySetupContext
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import io.restassured.path.json.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SarifOutputReportSpec : Spek({

    describe("sarif output report") {

        it("renders multiple issues") {
            val result = TestDetektion(
                createFinding(ruleName = "TestSmellA"),
                createFinding(ruleName = "TestSmellB"),
                createFinding(ruleName = "TestSmellC")
            )

            val report = SarifOutputReport().apply { init(EmptySetupContext()) }
            val jsonResult = report.render(result)
            val json = JsonPath.from(jsonResult)

            assertThat(json.getString("runs[0].tool.driver.name")).isEqualTo("detekt")
            assertThat(json.getString("runs[0].tool.driver.informationUri"))
                .isEqualTo("https://detekt.github.io/detekt")
            assertThat(json.getList<Any>("runs[0].results")).hasSize(3)
        }
    }
})

internal class TestVersionProvider : VersionProvider {

    override fun current(): String = "1.0.0"
}
