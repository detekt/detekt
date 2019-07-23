package io.gitlab.arturbosch.detekt

class BuildFailure(override val message: String?) : RuntimeException(message)
