package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue

class DelegatingResult(
    result: Detektion,
    override val issues: List<Issue>,
) : Detektion by result
