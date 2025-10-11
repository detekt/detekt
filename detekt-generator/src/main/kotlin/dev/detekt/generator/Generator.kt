package dev.detekt.generator

import dev.detekt.generator.collection.DetektCollector
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import java.io.PrintStream
import java.nio.file.Path
import kotlin.io.path.Path
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
        val time = measureTime {
            val session = buildStandaloneAnalysisAPISession {
                buildKtModuleProvider {
                    val targetPlatform = JvmPlatforms.defaultJvmPlatform
                    platform = targetPlatform
                    addModule(
                        buildKtSourceModule {
                            addSourceRoots(inputPaths)
                            platform = targetPlatform
                            moduleName = "source"
                        }
                    )
                }
            }

            val ktFiles = session.modulesWithFiles.values.flatten().map { it as KtFile }

            ktFiles.forEach(collector::visit)

            printer.print(collector.items)
        }

        outPrinter.println("\nGenerated all detekt documentation in $time.")
    }

    fun executeCustomRuleConfig() {
        val time = measureTime {
            val session = buildStandaloneAnalysisAPISession {
                buildKtModuleProvider {
                    val targetPlatform = JvmPlatforms.defaultJvmPlatform
                    platform = targetPlatform
                    inputPaths.forEach {
                        addModule(
                            buildKtSourceModule {
                                addSourceRoot(it.resolve("src/main/kotlin/"))
                                platform = targetPlatform
                                moduleName = it.toString()
                            }
                        )
                    }
                }
            }

            session.modulesWithFiles.forEach { (sourceModule, files) ->
                val collector = DetektCollector(textReplacements)
                files.forEach { file ->
                    collector.visit(file as KtFile)
                }
                printer.printCustomRuleConfig(
                    collector.items,
                    Path(sourceModule.name).resolve("src/main/resources/config/")
                )
            }
        }

        outPrinter.println("\nGenerated custom rules config in $time.")
    }
}
