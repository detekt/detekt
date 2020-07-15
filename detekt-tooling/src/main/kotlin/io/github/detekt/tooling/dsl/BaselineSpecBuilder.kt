package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.BaselineSpec
import java.nio.file.Path

@ProcessingModelDsl
class BaselineSpecBuilder : Builder<BaselineSpec>, BaselineSpec {

    override var path: Path? = null
    override var shouldCreateDuringAnalysis: Boolean = false

    override fun build(): BaselineSpec = BaselineModel(path, shouldCreateDuringAnalysis)
}

internal data class BaselineModel(
    override val path: Path?,
    override val shouldCreateDuringAnalysis: Boolean
) : BaselineSpec
