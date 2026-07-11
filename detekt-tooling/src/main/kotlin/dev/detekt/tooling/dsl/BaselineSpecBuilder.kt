package dev.detekt.tooling.dsl

import dev.detekt.tooling.api.spec.BaselineSpec
import java.nio.file.Path

@ProcessingModelDsl
class BaselineSpecBuilder : Builder<BaselineSpec> {

    var path: Path? = null
    var fragmentDirectory: Path? = null
    var shouldCreateDuringAnalysis: Boolean = false

    override fun build(): BaselineSpec = BaselineModel(path, fragmentDirectory, shouldCreateDuringAnalysis)
}

private data class BaselineModel(
    override val path: Path?,
    override val fragmentDirectory: Path?,
    override val shouldCreateDuringAnalysis: Boolean,
) : BaselineSpec
