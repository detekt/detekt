package io.gitlab.arturbosch.detekt.cli.baseline

class InvalidBaselineState(msg: String, error: Throwable) : IllegalStateException(msg, error)
