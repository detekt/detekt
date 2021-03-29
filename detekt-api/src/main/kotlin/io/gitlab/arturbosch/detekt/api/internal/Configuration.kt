package io.gitlab.arturbosch.detekt.api.internal

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Configuration(
    val description: String,
)
