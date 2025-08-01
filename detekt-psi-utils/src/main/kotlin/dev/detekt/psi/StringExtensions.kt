package dev.detekt.psi

import java.net.URL

fun String.lastArgumentMatchesUrl(): Boolean {
    val lastArgument = trimEnd().split(Regex("\\s+")).last()
    return runCatching {
        @Suppress("DEPRECATION")
        URL(lastArgument).toURI()
    }.isSuccess
}

fun String.lastArgumentMatchesMarkdownUrlSyntax(): Boolean {
    val urlNonCapturingRegex = """(?:(?!\s).)+"""
    val markdownUrlTitleRegexStrWithBraces = """\([^\(\\\n]*(?:\\.[^\)\\\n]*)*\)"""
    // Below regex works for both " and ' by using captured group \1
    // It uses negative lookahead to use captured group \1 as to exclude it
    val markdownUrlTitleRegexStrWithSingleOrDoubleQuotes = """(["'])(?:(?!(?:\1|\\)).)*(?:\\.(?:(?!(?:\1|\\)).)*)*\1"""
    val markdownUrlTitleRegex =
        """(?:$markdownUrlTitleRegexStrWithSingleOrDoubleQuotes|$markdownUrlTitleRegexStrWithBraces)"""
    val regex =
        """\[.+\]\($urlNonCapturingRegex(?:\s+$markdownUrlTitleRegex)?\s*\)$""".toRegex()
    return trimEnd().contains(regex)
}

fun String.lastArgumentMatchesKotlinReferenceUrlSyntax(): Boolean {
    val regex = """\[[\w|\.]*\]$""".toRegex()
    return trimEnd().contains(regex)
}
