package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.tooling.api.Baseline
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.RuleSet

internal class BaselineFilteredResult(
    result: Detektion,
    private val baseline: Baseline,
) : Detektion by result {

    override val findings: Map<RuleSet.Id, List<Finding2>> = result.findings
        .mapValues { (_, findings) -> findings.filterNot { baseline.contains(it.baselineId) } }
}
