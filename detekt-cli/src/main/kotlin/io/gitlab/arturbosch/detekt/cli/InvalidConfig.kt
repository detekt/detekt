package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Notification

class InvalidConfig(val messages: List<Notification>) : RuntimeException()
