package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.nio.file.Path
import org.jetbrains.kotlin.psi.KtFile

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
    private val compiler = KtTreeCompiler.instance(settings)

    fun run(): Detektion {
        val notifications = mutableListOf<Notification>()
        val ktFiles = mutableListOf<KtFile>()
        val findings = HashMap<String, List<Finding>>()

        for (current in pathsToAnalyze) {
            val files = compiler.compile(current)

            processors.forEach { it.onStart(files) }
            findings.mergeSmells(detektor.run(files))
            if (saveSupported) {
                KtFileModifier(current).saveModifiedFiles(files) {
                    notifications.add(it)
                }
            }

            ktFiles.addAll(files)
        }

        val result = DetektResult(findings.toSortedMap())
        processors.forEach { it.onFinish(ktFiles, result) }
        return result
    }

    fun run(project: Path, files: List<KtFile>): Detektion = runOnFiles(project, files)

    private fun runOnFiles(current: Path, files: List<KtFile>): DetektResult {
        processors.forEach { it.onStart(files) }

        val findings = detektor.run(files)
        val detektion = DetektResult(findings.toSortedMap())
        if (saveSupported) {
            KtFileModifier(current).saveModifiedFiles(files) {
                detektion.add(it)
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
