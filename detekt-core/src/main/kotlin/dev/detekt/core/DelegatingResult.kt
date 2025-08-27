package dev.detekt.core

import dev.detekt.api.Detektion
import dev.detekt.api.Issue

class DelegatingResult(
    result: Detektion,
    override val issues: List<Issue>,
) : Detektion by result
