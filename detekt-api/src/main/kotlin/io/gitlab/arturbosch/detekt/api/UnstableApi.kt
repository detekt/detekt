package io.gitlab.arturbosch.detekt.api

/**
 * Experimental detekt api which may change on minor or patch versions.
 */
@RequiresOptIn
annotation class UnstableApi(val reason: String = "")
