package io.gitlab.arturbosch.detekt.api.internal

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Configuration(
    val description: String,
)
