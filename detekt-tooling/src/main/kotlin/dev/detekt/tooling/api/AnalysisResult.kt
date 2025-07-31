package dev.detekt.tooling.api

import dev.detekt.api.Detektion

interface AnalysisResult {

    val error: DetektError?

    val container: Detektion?
}
