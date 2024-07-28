package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.AnalysisMode
import io.github.detekt.tooling.api.spec.ProjectSpec
import java.nio.file.Path
import kotlin.io.path.Path

@ProcessingModelDsl
class ProjectSpecBuilder : Builder<ProjectSpec> {

    var basePath: Path = Path("")
    var inputPaths: Collection<Path> = emptyList()
    var analysisMode: AnalysisMode = AnalysisMode.light

    override fun build(): ProjectSpec = ProjectModel(basePath, inputPaths, analysisMode)
}

private data class ProjectModel(
    override val basePath: Path,
    override val inputPaths: Collection<Path>,
    override val analysisMode: AnalysisMode,
) : ProjectSpec
