package io.gitlab.arturbosch.detekt.rules

import java.net.URL

fun String.lastArgumentMatchesUrl(): Boolean {
    val lastArgument = trimEnd().split(Regex("\\s+")).last()
    return runCatching {
        URL(lastArgument).toURI()
    }.isSuccess
}

fun String.lastArgumentMatchesMarkdownUrlSyntax(): Boolean {
    val urlNonCapturingRegex = "(?:[^ ]*)"
    val markdownUrlTitleRegexStr = "\"[^\"\\\\\\n]*(?:\\\\.[^\"\\\\\\n]*)*\""
    val regex = "\\[.*\\]\\($urlNonCapturingRegex(?: $markdownUrlTitleRegexStr)?\\)$".toRegex()
    return trimEnd().contains(regex)
}

fun String.lastArgumentMatchesKotlinReferenceUrlSyntax(): Boolean {
    val regex = "\\[[\\w|\\.]*\\]$".toRegex()
    return trimEnd().contains(regex)
}
