package io.gitlab.arturbosch.detekt.cli.baseline

interface Listing<T> {

    val ids: Set<String>
}
