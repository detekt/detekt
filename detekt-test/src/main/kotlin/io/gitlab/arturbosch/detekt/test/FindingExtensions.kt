package io.gitlab.arturbosch.detekt.test

import dev.detekt.api.Finding
import dev.detekt.api.Location

val Finding.location: Location
    get() = entity.location
