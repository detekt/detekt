package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2

class DelegatingResult(
    result: Detektion,
    override val findings: List<Finding2>
) : Detektion by result
