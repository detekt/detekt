package io.gitlab.arturbosch.detekt.api.v2

interface ConsoleReporter  {

    val priority: Int
        get() = 0

    fun render(detektion: Detektion): String?
}
