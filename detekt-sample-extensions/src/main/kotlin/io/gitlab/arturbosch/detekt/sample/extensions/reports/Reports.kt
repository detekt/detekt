package io.gitlab.arturbosch.detekt.sample.extensions.reports

import dev.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.sample.extensions.processors.fqNamesKey

fun qualifiedNamesReport(detektion: Detektion): String? {
    val fqNames = detektion.userData[fqNamesKey.toString()] as Set<*>?
    if (fqNames.isNullOrEmpty()) return null

    return with(StringBuilder()) {
        for (name in fqNames) {
            append("$name\n")
        }
        toString()
    }
}
