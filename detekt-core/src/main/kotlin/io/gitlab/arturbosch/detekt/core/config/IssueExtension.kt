package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.reporting.filterAutoCorrectedIssues
import org.jetbrains.kotlin.com.intellij.openapi.util.Key

private val ISSUES_COUNT_KEY = Key.create<Int>("ISSUES_COUNT")
internal const val MAX_ISSUES_KEY: String = "maxIssues"

internal fun Detektion.getOrComputeIssueCount(config: Config): Int {
    val maybeAmount = this.getUserData(ISSUES_COUNT_KEY)
    return maybeAmount ?: computeIssueCount(config)
}

private fun Detektion.computeIssueCount(config: Config): Int {
    val smells = filterAutoCorrectedIssues(config).flatMap { it.value }
    return smells.count().also {
        this.putUserData(ISSUES_COUNT_KEY, it)
    }
}
