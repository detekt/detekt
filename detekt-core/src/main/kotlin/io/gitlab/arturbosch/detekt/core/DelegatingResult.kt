package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.RuleSet

class DelegatingResult(
    result: Detektion,
    override val findings: Map<RuleSet.Id, List<Finding2>>
) : Detektion by result
