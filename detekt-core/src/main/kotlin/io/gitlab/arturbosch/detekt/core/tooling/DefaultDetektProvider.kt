package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.DetektProvider
import io.github.detekt.tooling.api.spec.ProcessingSpec

class DefaultDetektProvider : DetektProvider {

    override fun get(processingSpec: ProcessingSpec): Detekt {
        return AnalysisFacade(processingSpec)
    }
}
