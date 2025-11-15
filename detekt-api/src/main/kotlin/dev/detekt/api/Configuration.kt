package dev.detekt.api

/**
 * Annotate the target to specify a configuration for [Rule] or [RuleSetProvider].
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Configuration(val description: String)
