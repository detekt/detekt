package io.gitlab.arturbosch.detekt.rules

import java.net.URL

fun String.lastArgumentMatchesUrl(): Boolean {
    val lastArgument = trimEnd().split(Regex("\\s+")).last()
    return runCatching {
        URL(lastArgument).toURI()
    }.isSuccess
}
