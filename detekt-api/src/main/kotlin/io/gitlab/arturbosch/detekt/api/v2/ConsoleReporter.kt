package io.gitlab.arturbosch.detekt.api.v2

interface ConsoleReporter {

    fun render(detektion: Detektion): String?
}
