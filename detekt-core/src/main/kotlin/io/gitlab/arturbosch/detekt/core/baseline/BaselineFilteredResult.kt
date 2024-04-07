package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.tooling.api.Baseline
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue

internal class BaselineFilteredResult(
    result: Detektion,
    private val baseline: Baseline,
) : Detektion by result {

    override val issues: List<Issue> = result.issues.filterNot { baseline.contains(it.baselineId) }
}
