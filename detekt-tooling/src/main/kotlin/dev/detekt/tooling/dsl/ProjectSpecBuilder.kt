package dev.detekt.tooling.dsl

import dev.detekt.tooling.api.AnalysisMode
import dev.detekt.tooling.api.spec.ProjectSpec
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute

@ProcessingModelDsl
class ProjectSpecBuilder : Builder<ProjectSpec> {

    var basePath: Path = Path("").absolute()
        set(value) {
            require(value.isAbsolute) { "basePath should be absolute" }
            field = value
        }
    var inputPaths: Collection<Path> = emptyList()
    var analysisMode: AnalysisMode = AnalysisMode.light
    var diagnostics: Boolean = false

    override fun build(): ProjectSpec = ProjectModel(basePath, inputPaths, analysisMode, diagnostics)
}

private data class ProjectModel(
    override val basePath: Path,
    override val inputPaths: Collection<Path>,
    override val analysisMode: AnalysisMode,
    override val diagnostics: Boolean,
) : ProjectSpec
