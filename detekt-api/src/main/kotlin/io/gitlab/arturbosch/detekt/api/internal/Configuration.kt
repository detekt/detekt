package io.gitlab.arturbosch.detekt.api.internal

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Configuration(
    val description: String,
)
