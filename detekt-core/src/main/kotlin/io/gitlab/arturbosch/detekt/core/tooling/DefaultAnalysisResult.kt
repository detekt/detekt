package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.DetektError
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.UnstableApi

@OptIn(UnstableApi::class)
class DefaultAnalysisResult(
    @UnstableApi override val container: Detektion?,
    override val error: DetektError? = null
) : AnalysisResult {

    init {
        require(container != null || error != null)
    }
}
