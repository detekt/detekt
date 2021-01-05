package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.ProjectSpec
import java.nio.file.Path

@ProcessingModelDsl
class ProjectSpecBuilder : Builder<ProjectSpec>, ProjectSpec {

    override var basePath: Path? = null
    override var inputPaths: Collection<Path> = emptyList()
    override var excludes: Collection<String> = emptyList()
    override var includes: Collection<String> = emptyList()

    override fun build(): ProjectSpec = ProjectModel(basePath, inputPaths, excludes, includes)
}

internal data class ProjectModel(
    override val basePath: Path?,
    override val inputPaths: Collection<Path>,
    override val excludes: Collection<String>,
    override val includes: Collection<String>
) : ProjectSpec
