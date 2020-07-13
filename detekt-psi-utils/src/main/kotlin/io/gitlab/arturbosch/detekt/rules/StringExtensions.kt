package io.gitlab.arturbosch.detekt.rules

import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

fun String.lastArgumentMatchesUrl(): Boolean {
    val lastArgument = trimEnd().split(Regex("\\s+")).last()
    return try {
        URL(lastArgument).toURI()
        true
    } catch (e: MalformedURLException) {
        false
    } catch (e: URISyntaxException) {
        false
    }
}
