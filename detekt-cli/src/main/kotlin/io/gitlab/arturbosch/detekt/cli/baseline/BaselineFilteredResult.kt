package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

class BaselineFilteredResult(
    result: Detektion,
    private val baseline: Baseline
) : Detektion by result {

    override val findings: Map<RuleSetId, List<Finding>> = result.findings
        .mapValues { (_, findings) -> findings.filterNot { baseline.contains(it.baselineId) } }
}
