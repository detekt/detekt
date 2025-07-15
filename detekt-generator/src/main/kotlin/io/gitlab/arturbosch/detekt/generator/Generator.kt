package io.gitlab.arturbosch.detekt.generator

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.generator.collection.DetektCollector
import org.jetbrains.kotlin.psi.KtFile
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.streams.asSequence
import kotlin.time.measureTime

class Generator(
    private val inputPaths: List<Path>,
    private val textReplacements: Map<String, String>,
    documentationPath: Path?,
    configPath: Path?,
    private val outPrinter: PrintStream = System.out,
) {
    private val collector = DetektCollector(textReplacements)
    private val printer = DetektPrinter(documentationPath, configPath)

    fun execute() {
        val parser = KtCompiler()
        val time = measureTime {
            val ktFiles = inputPaths
                .flatMap { parseAll(parser, it) }

            ktFiles.forEach(collector::visit)

            printer.print(collector.items)
        }

        outPrinter.println("\nGenerated all detekt documentation in $time.")
    }

    fun executeCustomRuleConfig() {
        val parser = KtCompiler()
        val time = measureTime {
            inputPaths
                .map { parseAll(parser, it.resolve("src/main/kotlin/")) to it }
                .forEach { (list: Collection<KtFile>, folder: Path) ->
                    val collector = DetektCollector(textReplacements)
                    list.forEach { file ->
                        collector.visit(file)
                    }
                    printer.printCustomRuleConfig(
                        collector.items,
                        folder.resolve("src/main/resources/config/")
                    )
                }
        }

        outPrinter.println("\nGenerated custom rules config in $time.")
    }
}

private fun parseAll(parser: KtCompiler, root: Path): Collection<KtFile> =
    Files.walk(root)
        .asSequence()
        .filter { it.extension == "kt" }
        .map { parser.compile(it) }
        .toList()
