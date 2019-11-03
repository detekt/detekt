package io.gitlab.arturbosch.detekt.cli

class InvalidConfig(override val message: String?) : RuntimeException(message, null, true, false)
