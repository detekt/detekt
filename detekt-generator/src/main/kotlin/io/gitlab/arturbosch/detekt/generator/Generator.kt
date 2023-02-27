package io.gitlab.arturbosch.detekt.generator

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.generator.collection.DetektCollector
import io.gitlab.arturbosch.detekt.generator.printer.CliOptionsPrinter
import org.jetbrains.kotlin.psi.KtFile
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.streams.asSequence
import kotlin.system.measureTimeMillis

class Generator(
    private val arguments: GeneratorArgs,
    private val outPrinter: PrintStream = System.out
) {
    private val collector = DetektCollector()
    private val printer = DetektPrinter(arguments)
    private val cliOptionsPrinter = CliOptionsPrinter()

    private fun parseAll(parser: KtCompiler, root: Path): Collection<KtFile> =
        Files.walk(root)
            .asSequence()
            .filter { it.extension == "kt" }
            .map { parser.compile(root, it) }
            .toList()

    fun execute() {
        val parser = KtCompiler()
        val time = measureTimeMillis {
            val ktFiles = arguments.inputPath
                .flatMap { parseAll(parser, it) }

            ktFiles.forEach(collector::visit)

            printer.print(collector.items)

            cliOptionsPrinter.print(arguments.cliOptionsPath)
        }

        outPrinter.println("\nGenerated all detekt documentation in $time ms.")
    }

    fun executeCustomRuleConfig() {
        val parser = KtCompiler()
        val time = measureTimeMillis {
            arguments.inputPath
                .map { parseAll(parser, it.resolve("src/main/kotlin/")) to it }
                .forEach { (list: Collection<KtFile>, folder: Path) ->
                    val collector = DetektCollector()
                    list.forEach { file ->
                        collector.visit(file)
                    }
                    printer.printCustomRuleConfig(
                        collector.items,
                        folder.resolve("src/main/resources/config/")
                    )
                }
        }

        outPrinter.println("\nGenerated custom rules config in $time ms.")
    }
}
