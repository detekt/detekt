package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSet

class DelegatingResult(
    result: Detektion,
    override val findings: Map<RuleSet.Id, List<Finding>>
) : Detektion by result
