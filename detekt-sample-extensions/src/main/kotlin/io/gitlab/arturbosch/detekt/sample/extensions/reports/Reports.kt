package io.gitlab.arturbosch.detekt.sample.extensions.reports

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.sample.extensions.processors.fqNamesKey

fun qualifiedNamesReport(detektion: Detektion): String? {
    val fqNames = detektion.getData(fqNamesKey)
    println("fqNames: $fqNames")
    if (fqNames.isNullOrEmpty()) return null

    return with(StringBuilder()) {
        for (name in fqNames) {
            append("$name\n")
        }
        toString()
    }
}
