package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.generator.collection.DetektCollector
import io.gitlab.arturbosch.detekt.generator.printer.DetektPrinter
import kotlin.system.measureTimeMillis

/**
 * @author Marvin Ramin
 */
class Runner(private val arguments: Args) {
	private val listeners = listOf(DetektProgressListener())
	private val collector = DetektCollector()
	private val printer = DetektPrinter(arguments)

	fun execute() {
		val time = measureTimeMillis {
			val ktFiles = KtTreeCompiler(arguments.inputPath).compile()
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

