package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Interface to let the core know that this [Rule] needs a [BindingContext] instance
 *
 * The core will only run your [Rule] if it is running in FullAnalysis mode.
 */
interface RequiresFullAnalysis
