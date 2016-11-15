package io.gitlab.arturbosch.detekt.api

/**
 * Marks an member as experimental feature which may be changed or removed in the future.
 *
 * @author Artur Bosch
 */
annotation class Unstable(val value: String = "")