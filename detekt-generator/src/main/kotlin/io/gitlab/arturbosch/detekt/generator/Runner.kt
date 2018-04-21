package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.generator.collection.DetektCollector
import io.gitlab.arturbosch.detekt.generator.printer.DetektPrinter
import kotlin.system.measureTimeMillis

/**
 * @author Marvin Ramin
 * @author Artur Bosch
 */
class Runner(private val arguments: GeneratorArgs) {
	private val listeners = listOf(DetektProgressListener())
	private val collector = DetektCollector()
	private val printer = DetektPrinter(arguments)

	fun execute() {
		val time = measureTimeMillis {
			val compiler = KtTreeCompiler()
			val ktFiles = arguments.inputPath
					.flatMap { compiler.compile(it) }
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

