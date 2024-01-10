package io.gitlab.arturbosch.detekt.api

/**
 * Annotated [Rule] requires type resolution to work.
 *
 * The detekt core will honor this annotation and it will not run any rule with this annotation if the bindingContext
 * is empty.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresTypeResolution
