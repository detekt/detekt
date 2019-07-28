package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.generator.collection.DetektCollector
import io.gitlab.arturbosch.detekt.generator.printer.DetektPrinter
import java.nio.file.Path
import kotlin.system.measureTimeMillis

class Runner(private val arguments: GeneratorArgs) {
    private val listeners = listOf(DetektProgressListener())
    private val collector = DetektCollector()
    private val printer = DetektPrinter(arguments)

    private fun createCompiler(path: Path) =
        KtTreeCompiler.instance(ProcessingSettings(path))

    fun execute() {
        val time = measureTimeMillis {
            val ktFiles = arguments.inputPath
                .flatMap { createCompiler(it).compile(it) }
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
