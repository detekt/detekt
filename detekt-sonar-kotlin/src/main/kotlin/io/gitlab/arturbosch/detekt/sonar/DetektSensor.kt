package io.gitlab.arturbosch.detekt.sonar

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter
import io.gitlab.arturbosch.detekt.core.COMPLEXITY_KEY
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.Detektion
import io.gitlab.arturbosch.detekt.core.LLOC_KEY
import io.gitlab.arturbosch.detekt.core.PathFilter
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.Sensor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.batch.sensor.issue.NewIssue

/**
 * @author Artur Bosch
 */
class DetektSensor : Sensor {

	override fun describe(descriptor: SensorDescriptor) {
		descriptor.name(DETEKT_SENSOR).onlyOnLanguage(KOTLIN_KEY)
	}

	override fun execute(context: SensorContext) {
		val fileSystem = context.fileSystem()
		val baseDir = fileSystem.baseDir()

		val filters = ".*/test/.*,.*/resources/.*,.*/build/.*".split(",").map { PathFilter(it) }
		val config = YamlConfig.loadResource(ClasspathResourceConverter().convert("/default-detekt-config.yml"))
		val settings = ProcessingSettings(baseDir.toPath(), config = config, pathFilters = filters)

		val detektor = DetektFacade.instance(settings)
		val detektion = detektor.run()

		projectIssues(detektion, context)
		projectMetrics(detektion, context)
	}

	private fun projectIssues(detektion: Detektion, context: SensorContext) {
		val fileSystem = context.fileSystem()
		val baseDir = fileSystem.baseDir()
		detektion.findings.forEach { ruleSet, findings ->
			println("RuleSet: $ruleSet - ${findings.size}")
			findings.forEach { issue ->
				println(issue.compact())
				val inputFile = fileSystem.inputFile(fileSystem.predicates().`is`(baseDir.resolve(issue.location.file)))
				if (inputFile != null) {
					val newIssue = context.newIssue()
							.forRule(findKey(issue.id))
							.gap(2.0) // TODO how to setup?
							.primaryLocation(issue, inputFile)
					println(newIssue)
					newIssue.save()
				} else {
					println("No file found for ${issue.location.file}")
				}
			}
		}
	}

	private fun NewIssue.primaryLocation(issue: Finding, inputFile: InputFile): NewIssue {
		val (line, _) = issue.startPosition
		val newIssueLocation = newLocation()
				.on(inputFile)
				.at(inputFile.selectLine(line))
				.message("What does this do?")
		return this.at(newIssueLocation)
	}

	private fun projectMetrics(detektion: Detektion, context: SensorContext) {
		detektion.getData(COMPLEXITY_KEY)?.let {
			context.newMeasure<Int>()
					.withValue(it)
					.forMetric(MCCABE_PROJECT)
					.on(context.module())
					.save()
		}
		detektion.getData(LLOC_KEY)?.let {
			context.newMeasure<Int>()
					.withValue(it)
					.forMetric(LLOC_PROJECT)
					.on(context.module())
					.save()
		}
	}
}
