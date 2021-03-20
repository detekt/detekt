package io.gitlab.arturbosch.detekt.api.internal

/**
 * A Rule can be annotated with this to indicate that it needs type resolution to be enabled in order to work.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RequiresTypeResolution
