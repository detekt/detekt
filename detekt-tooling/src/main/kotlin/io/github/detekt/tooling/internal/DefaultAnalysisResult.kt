package io.github.detekt.tooling.internal

import dev.detekt.api.Detektion
import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.DetektError

class DefaultAnalysisResult(
    override val container: Detektion?,
    override val error: DetektError? = null,
) : AnalysisResult {

    init {
        require(container != null || error != null)
    }
}
