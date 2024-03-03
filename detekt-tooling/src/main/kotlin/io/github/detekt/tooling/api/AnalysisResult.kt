package io.github.detekt.tooling.api

import io.gitlab.arturbosch.detekt.api.Detektion

interface AnalysisResult {

    val error: DetektError?

    val container: Detektion?
}
