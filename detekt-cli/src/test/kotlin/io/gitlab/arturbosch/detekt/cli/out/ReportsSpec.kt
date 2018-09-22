package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.ReportLocator
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.util.function.Predicate

/**
 * @author Artur Bosch
 */
internal class ReportsSpec : Spek({

	given("arguments for jcommander") {

		val reportUnderTest = TestOutputReport::class.java.simpleName
		val args = arrayOf(
				"--input", "/tmp/must/be/given",
				"--report", "xml:/tmp/path1",
				"--report", "plain:/tmp/path2",
				"--report", "$reportUnderTest:/tmp/path3",
				"--report", "html:D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html"
		)
		val (cli, _) = parseArguments<CliArgs>(args)

		val reports = cli.reportPaths

		it("should parse multiple report entries") {
			assertThat(reports).hasSize(4)
		}

		val extensions = ReportLocator(ProcessingSettings(listOf())).load()
		val extensionsIds = extensions.mapTo(HashSet()) { it.id }

		it("should be able to convert to output reports") {
			assertThat(reports).allMatch { it.kind in extensionsIds }
		}

		it("should recognize custom output format") {
			assertThat(reports).haveExactly(1,
					Condition(Predicate { it.kind == reportUnderTest },
							"Corresponds exactly to the test output report."))

			assertThat(extensions).haveExactly(1,
					Condition(Predicate { it is TestOutputReport && it.ending == "yml" },
							"Is exactly the test output report."))
		}
	}
})

internal class TestOutputReport : OutputReport() {
	override val ending: String = "yml"
	override fun render(detektion: Detektion): String? {
		throw UnsupportedOperationException("not implemented")
	}
}
