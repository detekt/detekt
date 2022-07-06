package io.gitlab.arturbosch.detekt.formatting

internal fun String.toQualifiedRuleId() =
    if (contains(":")) {
        this
    } else {
        "standard:$this"
    }.trim()
