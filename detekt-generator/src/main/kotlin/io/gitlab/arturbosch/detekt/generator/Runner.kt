package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.generator.collection.DetektCollector
import io.gitlab.arturbosch.detekt.generator.printer.DetektPrinter
import java.nio.file.Path
import kotlin.system.measureTimeMillis

/**
 * @author Marvin Ramin
 */
class Runner(val path: Path) {
	private val listeners = listOf(DetektProgressListener())
	private val collector = DetektCollector()
	private val printer = DetektPrinter()

	fun execute() {
		val time = measureTimeMillis {
			val ktFiles = KtTreeCompiler(path).compile()
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

