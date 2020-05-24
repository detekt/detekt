package io.gitlab.arturbosch.detekt.generator

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.generator.collection.DetektCollector
import io.gitlab.arturbosch.detekt.generator.printer.DetektPrinter
import org.jetbrains.kotlin.psi.KtFile
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.system.measureTimeMillis

class Generator(
    private val arguments: GeneratorArgs,
    private val outPrinter: PrintStream = System.out
) {
    private val collector = DetektCollector()
    private val printer = DetektPrinter(arguments)

    private fun parseAll(parser: KtCompiler, root: Path): Collection<KtFile> =
        Files.walk(root)
            .filter { it.fileName.toString().endsWith(".kt") }
            .map { parser.compile(root, it) }
            .collect(Collectors.toList())

    fun execute() {
        val parser = KtCompiler()
        val time = measureTimeMillis {
            val ktFiles = arguments.inputPath
                .flatMap { parseAll(parser, it) }

            ktFiles.forEach(collector::visit)

            printer.print(collector.items)
        }

        outPrinter.println("\nGenerated all detekt documentation in $time ms.")
    }
}
