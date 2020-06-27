package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.ExitStatus
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.UnstableApi

@OptIn(UnstableApi::class)
class DefaultAnalysisResult(
    override val status: ExitStatus,
    override val error: Throwable?,
    @UnstableApi override val container: Detektion,
) : AnalysisResult
