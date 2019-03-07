package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 */
data class Whitelist(
    override val ids: Set<String>
) : Listing<Whitelist> {

    override fun toString(): String {
        return "Blacklist(ids=$ids)"
    }
}
