package io.gitlab.arturbosch.detekt.cli.baseline

data class Blacklist(
    override val ids: Set<String>
) : Listing<Blacklist>
