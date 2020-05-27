package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

class BaselineFilteredResult(
    result: Detektion,
    baselineFacade: BaselineFacade
) : Detektion by result {

    private val filteredFindings: Map<RuleSetId, List<Finding>>

    init {
        filteredFindings = result.findings
                .map { (key, value) -> key to baselineFacade.filter(value) }
                .toMap()
    }

    override val findings = filteredFindings
}
