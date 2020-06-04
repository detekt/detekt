package io.gitlab.arturbosch.detekt.core.config

class InvalidConfig(override val message: String?) : RuntimeException(message, null, true, false)
