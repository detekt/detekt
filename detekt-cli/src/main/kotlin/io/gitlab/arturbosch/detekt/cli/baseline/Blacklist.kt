package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 */
data class Blacklist(
    override val ids: Set<String>
) : Listing<Blacklist> {

    override fun toString(): String {
        return "Blacklist(ids=$ids)"
    }
}
