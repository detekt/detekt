package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.generator.collection.DetektCollector
import io.gitlab.arturbosch.detekt.generator.printer.DetektPrinter
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

private const val RULES_SOURCES_PATH = "./detekt-rules/src/main/kotlin"

/**
 * @author Marvin Ramin
 */
class Runner(private val arguments: Args) {

	private val listeners = listOf(DetektProgressListener())
	private val collector = DetektCollector()
	private val printer = DetektPrinter()

	fun execute() {
		val time = measureTimeMillis {
			val ktFiles = KtTreeCompiler(Paths.get(RULES_SOURCES_PATH)).compile()
			listeners.forEach { it.onStart(ktFiles) }

			ktFiles.forEach { file ->
						listeners.forEach { it.onProcess(file) }
						collector.visit(file)
					}

			printer.print(collector.items)
		}

		println("\nGenerated all detekt documentation in $time ms.")
	}
}

