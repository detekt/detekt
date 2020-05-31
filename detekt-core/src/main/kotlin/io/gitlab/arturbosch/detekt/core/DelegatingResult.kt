package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

class DelegatingResult(
    result: Detektion,
    override val findings: Map<RuleSetId, List<Finding>>
) : Detektion by result
