package io.github.detekt.compiler.plugin

import com.intellij.openapi.project.Project
import dev.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.compiler.plugin.internal.DetektService
import io.github.detekt.compiler.plugin.internal.info
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.Path

class DetektAnalysisExtension(
    private val log: MessageCollector,
    private val spec: ProcessingSpec,
    private val rootPath: Path,
    private val excludes: Collection<String>,
) : AnalysisHandlerExtension {

    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>,
    ): AnalysisResult? {
        if (spec.loggingSpec.debug) {
            log.info("$spec")
        }
        val matchers = excludes.map { FileSystems.getDefault().getPathMatcher("glob:$it") }
        val (includedFiles, excludedFiles) = files.partition { file ->
            matchers.none { it.matches(rootPath.relativize(Path(file.virtualFilePath))) }
        }
        log.info("Running detekt on module '${module.name.asString()}'")
        excludedFiles.forEach { log.info("File excluded by filter: ${it.virtualFilePath}") }
        DetektService(log, spec).analyze(includedFiles, bindingTrace.bindingContext)
        return null
    }
}
