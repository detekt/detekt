package io.gitlab.arturbosch.detekt.cli

class BuildFailure(override val message: String?) : RuntimeException(message, null, true, false)
