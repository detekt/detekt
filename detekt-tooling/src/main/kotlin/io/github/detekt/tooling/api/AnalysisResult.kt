package io.github.detekt.tooling.api

import io.gitlab.arturbosch.detekt.api.Detektion

interface AnalysisResult {

    val error: DetektError?

    val container: Detektion?
}

/**
 * May be used to exit the JVM via [kotlin.system.exitProcess].
 */
@Suppress("detekt.MagicNumber")
fun AnalysisResult.exitCode(): Int = when (error) {
    is UnexpectedError -> 1
    is IssuesFound -> 2
    is InvalidConfig -> 3
    null -> 0
}
