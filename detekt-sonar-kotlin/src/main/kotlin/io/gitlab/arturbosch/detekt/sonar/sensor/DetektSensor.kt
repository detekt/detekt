package io.gitlab.arturbosch.detekt.sonar.sensor

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.COMPLEXITY_KEY
import io.gitlab.arturbosch.detekt.core.Detektion
import io.gitlab.arturbosch.detekt.core.LLOC_KEY
import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_SENSOR
import io.gitlab.arturbosch.detekt.sonar.foundation.KOTLIN_KEY
import io.gitlab.arturbosch.detekt.sonar.foundation.KotlinSyntax
import io.gitlab.arturbosch.detekt.sonar.foundation.LOG
import io.gitlab.arturbosch.detekt.sonar.rules.findKey
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
		val detektor = configureDetektor(context)
		val detektion = detektor.run()

		highlightFiles(context)
		reportIssues(detektion, context)
		reportMetrics(detektion, context)
	}

	private fun highlightFiles(context: SensorContext) {
		val fileSystem = context.fileSystem()
		fileSystem.inputFiles {
			val language = it.language()
			language != null && language == KOTLIN_KEY
		}.forEach {
			KotlinSyntax.processFile(it, context)
		}
	}

	private fun reportIssues(detektion: Detektion, context: SensorContext) {
		val fileSystem = context.fileSystem()
		val baseDir = fileSystem.baseDir()
		detektion.findings.forEach { ruleSet, findings ->
			LOG.info("RuleSet: $ruleSet - ${findings.size}")
			findings.forEach { issue ->
				val inputFile = fileSystem.inputFile(fileSystem.predicates().`is`(baseDir.resolve(issue.location.file)))
				if (inputFile != null) {
					val newIssue = context.newIssue()
							.forRule(findKey(issue.id))
							.primaryLocation(issue, inputFile)
					newIssue.save()
				} else {
					LOG.info("No file found for ${issue.location.file}")
				}
			}
		}
	}

	private fun NewIssue.primaryLocation(issue: Finding, inputFile: InputFile): NewIssue {
		val line = issue.startPosition.line
		val newIssueLocation = newLocation()
				.on(inputFile)
				.at(inputFile.selectLine(line))
				.message(issue.issue.description)
		return this.at(newIssueLocation)
	}

	private fun reportMetrics(detektion: Detektion, context: SensorContext) {
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
