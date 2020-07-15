package io.github.detekt.tooling.api

sealed class DetektError(
    message: String?,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class MaxIssuesReached(message: String) : DetektError(message)

class InvalidConfig(message: String) : DetektError(message)

class UnexpectedError(override val cause: Throwable) : DetektError(null, cause)
