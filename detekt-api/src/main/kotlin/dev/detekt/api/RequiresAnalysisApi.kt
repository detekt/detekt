package dev.detekt.api

/**
 * Interface to let the core know that this [Rule] uses the Analysis API
 *
 * The core will only run your [Rule] if it is running in FullAnalysis mode.
 */
interface RequiresAnalysisApi
