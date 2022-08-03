package io.gitlab.arturbosch.detekt.api.internal

/**
 * Annotated [io.gitlab.arturbosch.detekt.api.Rule] requires type resolution to work.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresTypeResolution
