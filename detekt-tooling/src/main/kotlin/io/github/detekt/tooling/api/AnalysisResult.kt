package io.github.detekt.tooling.api

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.UnstableApi

interface AnalysisResult {

    val status: ExitStatus
    val error: Throwable?

    @UnstableApi // result type and name might change in the future
    val container: Detektion
}
