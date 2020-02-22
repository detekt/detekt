package io.gitlab.arturbosch.detekt.cli.config

class InvalidConfig(override val message: String?) : RuntimeException(message, null, true, false)
