package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class FindingsReportSpec : SubjectSpek<FindingsReport>({

	subject { FindingsReport() }

	given("several detekt findings") {

		it("reports the debt per ruleset and the overall debt") {
			val expectedContent = readResource("findings-report.txt")
			val detektion = object : TestDetektion() {
				override val findings: Map<String, List<Finding>> = mapOf(
						Pair("TestSmell", listOf(createFinding(), createFinding())),
						Pair("EmptySmells", emptyList()))
			}
			assertThat(subject.render(detektion)?.trimEnd()).isEqualTo(expectedContent)
		}

		it("reports no findings") {
			val detektion = TestDetektion()
			assertThat(subject.render(detektion))
		}
	}
})
