package io.github.detekt.tooling.api

sealed class DetektError(
    message: String?,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class MaxIssuesReached(message: String) : DetektError(message)

class InvalidConfig(message: String) : DetektError(message)

class UnexpectedError(message: String?, override val cause: Throwable) : DetektError(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}
