package io.gitlab.arturbosch.detekt.api.internal

/**
 * Specifies the first version of Detekt where a declaration has appeared.
 * Generally this should be used to indicate the version in which a rule was added.
 *
 * @property version the version in the following formats: `<major>.<minor>` or `<major>.<minor>.<patch>`
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class SinceDetekt(val version: String)
