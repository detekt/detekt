package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

internal class BaselineFilteredResult(
    result: Detektion,
    private val baseline: Baseline
) : Detektion by result {

    override val findings: Map<RuleSetId, List<Finding>> = result.findings
        .mapValues { (_, findings) -> findings.filterNot { baseline.contains(it.baselineId) } }
}
