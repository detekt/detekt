package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.BaselineSpec
import java.nio.file.Path

@ProcessingModelDsl
class BaselineSpecBuilder : Builder<BaselineSpec> {

    var path: Path? = null
    var shouldCreateDuringAnalysis: Boolean = false

    override fun build(): BaselineSpec = BaselineModel(path, shouldCreateDuringAnalysis)
}

private data class BaselineModel(
    override val path: Path?,
    override val shouldCreateDuringAnalysis: Boolean,
) : BaselineSpec
