package dev.detekt.core.tooling

import dev.detekt.tooling.api.Detekt
import dev.detekt.tooling.api.DetektProvider
import dev.detekt.tooling.api.spec.ProcessingSpec

class DefaultDetektProvider : DetektProvider {

    override fun get(processingSpec: ProcessingSpec): Detekt = AnalysisFacade(processingSpec)
}
