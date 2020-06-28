package io.github.detekt.tooling.api

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.UnstableApi

interface AnalysisResult {

    val error: DetektError?

    @UnstableApi // result type and name might change in the future
    val container: Detektion?

    /**
     * May be used to exit the jvm via [kotlin.system.exitProcess].
     */
    @Suppress("detekt.MagicNumber")
    fun exitCode(): Int = when (error) {
        is UnexpectedError -> 1
        is MaxIssuesReached -> 2
        is InvalidConfig -> 3
        else -> 0
    }
}
