package dev.detekt.tooling.internal

import dev.detekt.tooling.api.AnalysisResult
import dev.detekt.tooling.api.DetektError
import dev.detekt.api.Detektion

class DefaultAnalysisResult(
    override val container: Detektion?,
    override val error: DetektError? = null,
) : AnalysisResult {

    init {
        require(container != null || error != null)
    }
}
