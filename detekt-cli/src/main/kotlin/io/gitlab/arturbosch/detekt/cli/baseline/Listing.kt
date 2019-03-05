package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 */
interface Listing<T> {

    val ids: Set<String>
}
