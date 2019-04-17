package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.FormattingInfo
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.createCompilerConfiguration
import io.gitlab.arturbosch.detekt.api.createKotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class DetektFacade(
    private val detektor: Detektor,
    settings: ProcessingSettings,
    private val processors: List<FileProcessListener>
) {

    private val saveSupported = settings.config.valueOrDefault("autoCorrect", false)
    private val pathsToAnalyze = settings.inputPaths
    private val classpath = settings.classpath
    private val compilerConfiguration = createCompilerConfiguration(classpath, pathsToAnalyze)
    private val environment = createKotlinCoreEnvironment(compilerConfiguration)
    private val compiler = KtTreeCompiler.instance(settings)

    fun run(): Detektion {
        val formatting = mutableListOf<FormattingInfo>()
        val ktFiles = mutableListOf<KtFile>()
        val findings = HashMap<String, List<Finding>>()

        for (current in pathsToAnalyze) {
            val files = compiler.compile(current)

            processors.forEach { it.onStart(files) }
            findings.mergeSmells(detektor.run(files, environment, classpath.isNotEmpty()))
            KtFileModifier(current).saveModifiedFiles(files) {
                formatting.add(it)
            }

            ktFiles.addAll(files)
        }

        val result = DetektResult(findings.toSortedMap())
        result.formatting.addAll(formatting)

        if (saveSupported) {
            result.formatting.forEach {
                result.add(ModificationNotification(it.path))
                Files.write(it.path, it.formattedContent.toByteArray())
            }
        }

        processors.forEach { it.onFinish(ktFiles, result) }
        return result
    }

    fun run(project: Path, files: List<KtFile>): Detektion = runOnFiles(project, files)

    private fun runOnFiles(current: Path, files: List<KtFile>): DetektResult {
        processors.forEach { it.onStart(files) }

        val findings = detektor.run(files)
        val detektion = DetektResult(findings.toSortedMap())
        KtFileModifier(current).saveModifiedFiles(files) {
            detektion.formatting.add(it)
        }

        if (saveSupported) {
            detektion.formatting.forEach {
                detektion.add(ModificationNotification(it.path))
                Files.write(it.path, it.formattedContent.toByteArray())
            }
        }

        processors.forEach { it.onFinish(files, detektion) }
        return detektion
    }

    companion object {

        fun create(settings: ProcessingSettings): DetektFacade {
            val providers = RuleSetLocator(settings).load()
            val processors = FileProcessorLocator(settings).load()
            return create(settings, providers, processors)
        }

        fun create(settings: ProcessingSettings, vararg providers: RuleSetProvider): DetektFacade {
            return create(settings, providers.toList(), emptyList())
        }

        fun create(settings: ProcessingSettings, vararg processors: FileProcessListener): DetektFacade {
            return create(settings, emptyList(), processors.toList())
        }

        fun create(
            settings: ProcessingSettings,
            providers: List<RuleSetProvider>,
            processors: List<FileProcessListener>
        ): DetektFacade {
            return DetektFacade(Detektor(settings, providers, processors), settings, processors)
        }
    }
}
