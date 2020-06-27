package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.ExitStatus
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.core.DetektFacade
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import java.nio.file.Path

class DetektFacade(
    private val spec: ProcessingSpec,
) : Detekt {

    override fun run(): AnalysisResult {
        val container = spec.withSettings { DetektFacade.create(this).run() }
        return DefaultAnalysisResult(ExitStatus.NORMAL_RUN, null, container)
    }

    override fun run(path: Path): AnalysisResult {
        TODO()
    }

    override fun run(sourceCode: String, filename: String): AnalysisResult {
        TODO()
    }

    override fun run(files: Collection<KtFile>, bindingContext: BindingContext): AnalysisResult {
        TODO()
    }

    override fun run(files: Collection<KtFile>, bindingTrace: BindingTrace): AnalysisResult {
        TODO()
    }
}
