package io.gitlab.arturbosch.detekt.cli.baseline

data class Whitelist(
    override val ids: Set<String>
) : Listing<Whitelist> {

    override fun toString(): String {
        return "Whitelist(ids=$ids)"
    }
}
