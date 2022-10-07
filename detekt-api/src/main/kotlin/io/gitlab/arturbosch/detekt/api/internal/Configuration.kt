package io.gitlab.arturbosch.detekt.api.internal

/**
 * Annotate the target to specify a configuration for [io.gitlab.arturbosch.detekt.api.Rule] or
 * [io.gitlab.arturbosch.detekt.api.RuleSetProvider].
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Configuration(
    val description: String
)
